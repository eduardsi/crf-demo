package awsm.application.banking.impl;

import static awsm.application.banking.impl.Transactions.Tx.recordOfAccount;
import static awsm.infrastructure.modeling.Amount.of;
import static awsm.infrastructure.modeling.Amount.ZERO;
import static awsm.infrastructure.time.TimeMachine.today;
import static com.google.common.base.Preconditions.checkState;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;
import static jooq.tables.BankAccount.BANK_ACCOUNT;
import static jooq.tables.BankAccountTx.BANK_ACCOUNT_TX;

import awsm.application.banking.impl.Transactions.Tx;
import awsm.infrastructure.modeling.Amount;
import awsm.infrastructure.modeling.DomainEntity;
import java.time.LocalDate;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;
import jooq.tables.records.BankAccountRecord;
import jooq.tables.records.BankAccountTxRecord;
import org.jooq.DSLContext;
import org.springframework.stereotype.Component;

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

  public BankAccount(Type type, Iban iban, WithdrawalLimit withdrawalLimit) {
    this.iban = iban;
    this.type = type;
    this.withdrawalLimit = requireNonNull(withdrawalLimit, "Withdrawal limit is mandatory");
  }

  public Optional<Long> id() {
    return id;
  }

  public Tx withdraw(Amount amount) {
    new EnforceOpen();

    var tx = Tx.withdrawalOf(amount);
    var uncommittedTransactions = committedTransactions.with(tx);

    new EnforcePositiveBalance(uncommittedTransactions);
    new EnforceWithdrawalLimits(uncommittedTransactions);

    committedTransactions = uncommittedTransactions;

    return tx;
  }

  public Tx deposit(Amount amount) {
    new EnforceOpen();

    var tx = Tx.depositOf(amount);

    var uncommittedTransactions = committedTransactions.with(tx);

    committedTransactions = uncommittedTransactions;

    return tx;
  }

  public Amount balance() {
    return committedTransactions.balance();
  }

  public BankStatement statement(LocalDate from, LocalDate to) {
    return new BankStatement(from, to, committedTransactions);
  }

  public void close(UnsatisfiedObligations unsatisfiedObligations) {
    checkState(!unsatisfiedObligations.exist(), "Bank account cannot be closed because a holder has unsatified obligations");
    status = Status.CLOSED;
  }

  public void saveNew(Repository repository) {
    repository.insert(this);
    repository.insertTransactions(this);
  }

  public void save(Repository repository) {
    repository.update(this);
    repository.deleteTransactions(this);
    repository.insertTransactions(this);
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

    private Amount withdrawn(LocalDate someDay) {
      return transactions
          .thatAre(Tx.bookedOn(someDay))
          .thatAre(Tx.isWithdrawal())
          .balance()
          .abs();
    }
  }

  @Component
  static class Repository {

    private final DSLContext dsl;

    Repository(DSLContext dsl) {
      this.dsl = dsl;
    }

    private void update(BankAccount self) {
      dsl.update(BANK_ACCOUNT)
          .set(self.as(jooq()))
          .where(BANK_ACCOUNT.ID.equal(self.id.orElseThrow()))
          .execute();
    }

    private void insert(BankAccount self) {
      var id = dsl
              .insertInto(BANK_ACCOUNT)
              .set(self.as(jooq()))
              .returning(BANK_ACCOUNT.ID)
              .fetchOne()
              .getId();
      self.id = Optional.of(id);
    }

    private void insertTransactions(BankAccount self) {
      self.committedTransactions.stream()
          .map(recordOfAccount(self.id.orElseThrow()))
          .forEach(rec -> dsl
              .insertInto(BANK_ACCOUNT_TX)
              .set(rec)
              .execute());
    }

    private void deleteTransactions(BankAccount self) {
      dsl
          .deleteFrom(BANK_ACCOUNT_TX)
          .where(BANK_ACCOUNT_TX.BANK_ACCOUNT_ID.eq(self.id.orElseThrow()))
          .execute();
    }

    BankAccount singleBy(long id) {
      var jooqTransactions = dsl
              .selectFrom(BANK_ACCOUNT_TX)
              .where(BANK_ACCOUNT_TX.BANK_ACCOUNT_ID.equal(id))
              .orderBy(BANK_ACCOUNT_TX.INDEX.asc())
              .fetchStream();

      return dsl
          .selectFrom(BANK_ACCOUNT)
          .where(BANK_ACCOUNT.ID.equal(id))
          .fetchOptional()
          .map(fromJooq(jooqTransactions))
          .orElseThrow();


    }

    private Function<BankAccountRecord, BankAccount> fromJooq(Stream<BankAccountTxRecord> jooqTransactions) {
      return jooq -> {
        var self = new BankAccount(
            Type.valueOf(jooq.getType()),
            new Iban(jooq.getIban()),
            new WithdrawalLimit(Amount.of(jooq.getDailyLimit())));
        self.id = Optional.of(jooq.getId());
        self.status = Status.valueOf(jooq.getStatus());
        self.committedTransactions = new Transactions(jooqTransactions.map(Tx::new).collect(toList()));
        return self;
      };
    }

    private Function<BankAccount, BankAccountRecord> jooq() {
      return self -> new BankAccountRecord()
          .setIban(self.iban + "")
          .setStatus(self.status.name())
          .setType(self.type.name())
          .setDailyLimit(self.withdrawalLimit.dailyLimit().toBigDecimal());
    }

  }

}

