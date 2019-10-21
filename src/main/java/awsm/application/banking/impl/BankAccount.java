package awsm.application.banking.impl;

import static awsm.application.banking.impl.Transactions.unmodifiable;
import static awsm.application.trading.impl.$.$;
import static awsm.application.trading.impl.$.ZERO;
import static awsm.infrastructure.time.TimeMachine.today;
import static com.google.common.base.Preconditions.checkState;
import static java.util.Objects.requireNonNull;
import static jooq.tables.BankAccount.BANK_ACCOUNT;
import static jooq.tables.BankAccountTx.BANK_ACCOUNT_TX;

import awsm.application.trading.impl.$;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.sql.DataSource;
import org.jooq.SQLDialect;
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

  private final List<Transaction> committedTransactions;

  public BankAccount(Type type, WithdrawalLimit withdrawalLimit) {
    this.committedTransactions = new ArrayList<>();
    this.iban = Iban.newlyGenerated();
    this.type = type;
    this.status = Status.OPEN;
    this.withdrawalLimit = requireNonNull(withdrawalLimit, "Withdrawal limit is mandatory");
  }

  BankAccount(DataSource dataSource, long id) {
    var transactions = DSL.using(dataSource, SQLDialect.POSTGRES)
        .selectFrom(BANK_ACCOUNT_TX)
        .where(BANK_ACCOUNT_TX.BANK_ACCOUNT_ID.equal(id))
        .orderBy(BANK_ACCOUNT_TX.INDEX.asc())
        .fetchStream()
        .map(Transaction::new)
        .collect(Collectors.toList());

    var rec = DSL.using(dataSource, SQLDialect.POSTGRES)
        .selectFrom(BANK_ACCOUNT)
        .where(BANK_ACCOUNT.ID.equal(id))
        .fetchAny();

    this.committedTransactions = transactions;
    this.iban = new Iban(rec.getIban());
    this.status = Status.valueOf(rec.getStatus());
    this.type = Type.valueOf(rec.getType());
    this.withdrawalLimit = new WithdrawalLimit($(rec.getDailyLimit()));
  }

  public Transaction withdraw($ amount) {
    new EnforceOpen();

    var tx = Transaction.withdrawalOf(amount);
    var uncommittedTransactions = unmodifiable(committedTransactions).with(tx);

    new EnforcePositiveBalance(uncommittedTransactions);
    new EnforceWithdrawalLimits(uncommittedTransactions);

    committedTransactions.add(tx);

    return tx;
  }

  public Transaction deposit($ amount) {
    new EnforceOpen();

    var tx = Transaction.depositOf(amount);
    committedTransactions.add(tx);

    return tx;
  }

  public $ balance() {
    return unmodifiable(committedTransactions).balance();
  }

  public BankStatement statement(LocalDate from, LocalDate to) {
    return new BankStatement(from, to, unmodifiable(committedTransactions));
  }

  public void close(UnsatisfiedObligations unsatisfiedObligations) {
    checkState(!unsatisfiedObligations.exist(), "Bank account cannot be closed because a holder has unsatified obligations");
    status = Status.CLOSED;
  }

  long saveNew(DataSource dataSource) {
    var bankAccountId = DSL.using(dataSource, SQLDialect.POSTGRES)
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


    for (int i = 0; i < committedTransactions.size(); i++) {
      var tx = committedTransactions.get(i);
      DSL.using(dataSource, SQLDialect.POSTGRES)
          .insertInto(BANK_ACCOUNT_TX,
              BANK_ACCOUNT_TX.BANK_ACCOUNT_ID,
              BANK_ACCOUNT_TX.INDEX,
              BANK_ACCOUNT_TX.AMOUNT,
              BANK_ACCOUNT_TX.BOOKING_TIME,
              BANK_ACCOUNT_TX.TYPE)
          .values(bankAccountId, i, tx.amount().big(), tx.bookingTime(), tx.type().name())
          .execute();
    }

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

    private final Transactions uncommittedTransactions;

    private EnforcePositiveBalance(Transactions uncommittedTransactions) {
      this.uncommittedTransactions = uncommittedTransactions;
      checkState(isPositiveBalance(), "Not enough funds available on your account.");
    }

    private boolean isPositiveBalance() {
      return uncommittedTransactions.balance().isAtLeast(ZERO);
    }
  }

  private class EnforceWithdrawalLimits {

    private final Transactions uncommittedTransactions;

    private EnforceWithdrawalLimits(Transactions uncommittedTransactions) {
      this.uncommittedTransactions = uncommittedTransactions;
      var dailyLimit = withdrawalLimit.dailyLimit();
      var notExceeded = withdrawn(today()).isAtMost(dailyLimit);
      checkState(notExceeded, "Daily withdrawal limit (%s) reached.", dailyLimit);
    }

    private $ withdrawn(LocalDate someDay) {
      return uncommittedTransactions
          .thatAre(Transaction.bookedOn(someDay))
          .thatAre(Transaction.isWithdrawal())
          .balance()
          .abs();
    }
  }

}

