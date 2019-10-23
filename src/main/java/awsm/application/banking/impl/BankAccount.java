package awsm.application.banking.impl;

import static awsm.application.banking.impl.Transactions.Tx.recordOfAccount;
import static awsm.application.trading.impl.$.$;
import static awsm.application.trading.impl.$.ZERO;
import static awsm.infrastructure.time.TimeMachine.today;
import static com.google.common.base.Preconditions.checkState;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;
import static jooq.tables.BankAccount.BANK_ACCOUNT;
import static jooq.tables.BankAccountTx.BANK_ACCOUNT_TX;

import awsm.application.banking.impl.Transactions.Tx;
import awsm.application.trading.impl.$;
import awsm.infrastructure.modeling.DomainEntity;
import java.time.LocalDate;
import java.util.Optional;
import java.util.function.Function;
import jooq.tables.records.BankAccountRecord;
import org.jooq.DSLContext;

public class BankAccount implements DomainEntity<BankAccount> {

  enum Status {
    OPEN, CLOSED
  }

  public enum Type {
    CHECKING, SAVINGS
  }

  private Status status = Status.OPEN;

  private final Type type;

  private final WithdrawalLimit withdrawalLimit;

  private final Iban iban;

  private Transactions committedTransactions = Transactions.none();

  private Optional<Long> id = Optional.empty();

  public BankAccount(DSLContext dsl, long id) {
    var jooqAccount = dsl
        .selectFrom(BANK_ACCOUNT)
        .where(BANK_ACCOUNT.ID.equal(id))
        .fetchAny();

    this.committedTransactions = new Transactions(
        dsl
            .selectFrom(BANK_ACCOUNT_TX)
            .where(BANK_ACCOUNT_TX.BANK_ACCOUNT_ID.equal(id))
            .orderBy(BANK_ACCOUNT_TX.INDEX.asc())
            .fetchStream()
            .map(Tx::new)
            .collect(toList())
    );

    this.type = Type.valueOf(jooqAccount.getType());
    this.iban = new Iban(jooqAccount.getIban());
    this.id = Optional.of(jooqAccount.getId());
    this.status = Status.valueOf(jooqAccount.getStatus());
    this.withdrawalLimit = new WithdrawalLimit($(jooqAccount.getDailyLimit()));
  }

  public BankAccount(Type type, Iban iban, WithdrawalLimit withdrawalLimit) {
    this.iban = iban;
    this.type = type;
    this.withdrawalLimit = requireNonNull(withdrawalLimit, "Withdrawal limit is mandatory");
  }

  public Optional<Long> id() {
    return id;
  }

  public Tx withdraw($ amount) {
    new EnforceOpen();

    var tx = Tx.withdrawalOf(amount);
    var uncommittedTransactions = committedTransactions.with(tx);

    new EnforcePositiveBalance(uncommittedTransactions);
    new EnforceWithdrawalLimits(uncommittedTransactions);

    committedTransactions = uncommittedTransactions;

    return tx;
  }

  public Tx deposit($ amount) {
    new EnforceOpen();

    var tx = Tx.depositOf(amount);

    var uncommittedTransactions = committedTransactions.with(tx);

    committedTransactions = uncommittedTransactions;

    return tx;
  }

  public $ balance() {
    return committedTransactions.balance();
  }

  public BankStatement statement(LocalDate from, LocalDate to) {
    return new BankStatement(from, to, committedTransactions);
  }

  public void close(UnsatisfiedObligations unsatisfiedObligations) {
    checkState(!unsatisfiedObligations.exist(), "Bank account cannot be closed because a holder has unsatified obligations");
    status = Status.CLOSED;
  }

  public void saveNew(DSLContext dsl) {
    new InsertBankAccount(dsl);
    new InsertTransactions(dsl);
  }

  public void save(DSLContext dsl) {
    new UpdateBankAccount(dsl);
    new DeleteTransactions(dsl);
    new InsertTransactions(dsl);
  }

  private class EnforceOpen {
    private EnforceOpen() {
      checkState(isOpen(), "Account is closed.");
    }

    private boolean isOpen() {
      return status.equals(Status.OPEN);
    }
  }

  private static class EnforcePositiveBalance {

    private final Transactions transactions;

    private EnforcePositiveBalance(Transactions transactions) {
      this.transactions = transactions;
      checkState(isPositiveBalance(), "Not enough funds available on your account.");
    }

    private boolean isPositiveBalance() {
      return transactions.balance().isAtLeast(ZERO);
    }
  }

  private class EnforceWithdrawalLimits {

    private final Transactions transactions;

    private EnforceWithdrawalLimits(Transactions transactions) {
      this.transactions = transactions;
      var dailyLimit = withdrawalLimit.dailyLimit();
      var notExceeded = withdrawn(today()).isAtMost(dailyLimit);
      checkState(notExceeded, "Daily withdrawal limit (%s) reached.", dailyLimit);
    }

    private $ withdrawn(LocalDate someDay) {
      return transactions
          .thatAre(Tx.bookedOn(someDay))
          .thatAre(Tx.isWithdrawal())
          .balance()
          .abs();
    }
  }

  private class InsertTransactions {
    InsertTransactions(DSLContext dsl) {
      committedTransactions.stream()
          .map(recordOfAccount(id.orElseThrow()))
          .forEach(rec -> dsl
              .insertInto(BANK_ACCOUNT_TX)
              .set(rec)
              .execute());
    }
  }

  private class DeleteTransactions {
    DeleteTransactions(DSLContext dsl) {
      dsl
          .deleteFrom(BANK_ACCOUNT_TX)
          .where(BANK_ACCOUNT_TX.BANK_ACCOUNT_ID.eq(id.orElseThrow()))
          .execute();
    }
  }

  private class UpdateBankAccount {
    UpdateBankAccount(DSLContext dsl) {
      dsl.update(BANK_ACCOUNT)
          .set(as(new Record()))
          .where(BANK_ACCOUNT.ID.equal(id.orElseThrow()))
          .execute();
    }
  }

  private class InsertBankAccount {
    InsertBankAccount(DSLContext dsl) {
      id = Optional.of(
          dsl
              .insertInto(BANK_ACCOUNT)
              .set(as(new Record()))
              .returning(BANK_ACCOUNT.ID)
              .fetchOne()
              .getId()
      );
    }
  }

  private static class Record implements Function<BankAccount, BankAccountRecord> {
    @Override
    public BankAccountRecord apply(BankAccount it) {
      return new BankAccountRecord()
          .setIban(it.iban + "")
          .setStatus(it.status.name())
          .setType(it.type.name())
          .setDailyLimit(it.withdrawalLimit.dailyLimit().big());
    }
  }

}

