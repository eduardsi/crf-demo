package awsm.application.banking.impl;

import static awsm.application.trading.impl.$.$;
import static awsm.application.trading.impl.$.ZERO;
import static awsm.infrastructure.time.TimeMachine.today;
import static com.google.common.base.Preconditions.checkState;
import static java.util.Objects.requireNonNull;
import static jooq.tables.BankAccount.BANK_ACCOUNT;
import static org.jooq.SQLDialect.POSTGRES;

import awsm.application.trading.impl.$;
import java.time.LocalDate;
import java.util.Optional;
import javax.sql.DataSource;
import jooq.tables.records.BankAccountRecord;
import org.jooq.impl.DSL;

public class BankAccount {

  enum Status {
    OPEN, CLOSED
  }

  public enum Type {
    CHECKING, SAVINGS
  }

  private Status status;

  @SuppressWarnings("unused")
  private final Type type;

  private final WithdrawalLimit withdrawalLimit;

  private final Iban iban;

  private Transactions committedTransactions;

  private Optional<Long> pk = Optional.empty();

  public BankAccount(Type type, WithdrawalLimit withdrawalLimit) {
    this.committedTransactions = Transactions.none();
    this.iban = Iban.newlyGenerated();
    this.type = type;
    this.status = Status.OPEN;
    this.withdrawalLimit = requireNonNull(withdrawalLimit, "Withdrawal limit is mandatory");
  }

  BankAccount(DataSource dataSource, long id) {
    var rec = DSL.using(dataSource, POSTGRES)
        .selectFrom(BANK_ACCOUNT)
        .where(BANK_ACCOUNT.ID.equal(id))
        .fetchAny();

    this.pk = Optional.of(id);
    this.committedTransactions = new Transactions(dataSource, id);
    this.iban = new Iban(rec.getIban());
    this.status = Status.valueOf(rec.getStatus());
    this.type = Type.valueOf(rec.getType());
    this.withdrawalLimit = new WithdrawalLimit($(rec.getDailyLimit()));
  }

  public Transaction withdraw($ amount) {
    new EnforceOpen();

    var tx = Transaction.withdrawalOf(amount);
    var uncommittedTransactions = committedTransactions.with(tx);

    new EnforcePositiveBalance(uncommittedTransactions);
    new EnforceWithdrawalLimits(uncommittedTransactions);

    committedTransactions = uncommittedTransactions;

    return tx;
  }

  public Transaction deposit($ amount) {
    new EnforceOpen();

    var tx = Transaction.depositOf(amount);

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

  public void save(DataSource dataSource) {
    var pk = this.pk.orElseThrow();
    BankAccountRecord rec = new BankAccountRecord(
        pk,
        iban + "",
        status.name(),
        type.name(),
        withdrawalLimit.dailyLimit().big());

    DSL.using(dataSource, POSTGRES)
        .executeUpdate(rec);

    committedTransactions.save(dataSource, pk);
  }

  long saveNew(DataSource dataSource) {
    var bankAccountId = DSL.using(dataSource, POSTGRES)
        .insertInto(BANK_ACCOUNT,
            BANK_ACCOUNT.IBAN,
            BANK_ACCOUNT.STATUS,
            BANK_ACCOUNT.TYPE,
            BANK_ACCOUNT.DAILY_LIMIT)
        .values(
            iban + "",
            status.name(),
            type.name(),
            withdrawalLimit.dailyLimit().big())
        .returning(BANK_ACCOUNT.ID)
        .fetchOne()
        .getId();

    committedTransactions.save(dataSource, bankAccountId);

    return bankAccountId;
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
          .thatAre(Transaction.bookedOn(someDay))
          .thatAre(Transaction.isWithdrawal())
          .balance()
          .abs();
    }
  }

}

