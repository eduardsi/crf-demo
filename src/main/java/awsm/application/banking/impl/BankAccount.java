package awsm.application.banking.impl;

import static awsm.application.banking.impl.Transactions.unmodifiable;
import static awsm.application.trading.impl.$.$;
import static awsm.application.trading.impl.$.ZERO;
import static awsm.infrastructure.time.TimeMachine.today;
import static com.google.common.base.Preconditions.checkState;
import static java.util.Objects.requireNonNull;

import awsm.application.trading.impl.$;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class BankAccount {

  enum Status {
    OPEN, CLOSED
  }

  public enum Type {
    CHECKING, SAVINGS
  }

  private Long id;

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

  BankAccount(ResultSet rs, List<Transaction> transactions) throws SQLException {
    this.committedTransactions = transactions;
    this.iban = new Iban(rs.getString("iban"));
    this.status = Status.valueOf(rs.getString("status"));
    this.type = Type.valueOf(rs.getString("type"));
    this.withdrawalLimit = new WithdrawalLimit($(rs.getBigDecimal("daily_limit")));
  }

  Iban iban() {
    return iban;
  }

  Status status() {
    return status;
  }

  Type type() {
    return type;
  }

  WithdrawalLimit withdrawalLimit() {
    return withdrawalLimit;
  }

  List<Transaction> committedTransactions() {
    return committedTransactions;
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

