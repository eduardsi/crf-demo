package awsm.domain.banking;

import static awsm.domain.banking.Transaction.Type.DEPOSIT;
import static awsm.domain.banking.Transaction.Type.WITHDRAWAL;
import static awsm.domain.offers.$.ZERO;
import static awsm.infra.time.TimeMachine.clock;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.base.Predicates.and;
import static java.time.LocalDate.now;
import static java.util.Objects.requireNonNull;

import awsm.domain.offers.$;
import awsm.infra.hibernate.HibernateConstructor;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import javax.persistence.CollectionTable;
import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OrderColumn;
import javax.persistence.Version;

@Entity
public class BankAccount {

  enum Status {
    OPEN, CLOSED
  }

  @Id
  @Nullable
  @GeneratedValue
  private Long id;

  @Enumerated(EnumType.STRING)
  private Status status = Status.OPEN;

  @Embedded
  private WithdrawalLimit withdrawalLimit;

  @ElementCollection
  @CollectionTable(name = "BANK_ACCOUNT_TX")
  @OrderColumn(name = "INDEX")
  private List<Transaction> transactions = new ArrayList<>();

  @Version
  private long version;

  public BankAccount(WithdrawalLimit withdrawalLimit) {
    this.withdrawalLimit = requireNonNull(withdrawalLimit, "Withdrawal limit is mandatory");
  }

  @HibernateConstructor
  private BankAccount() {
  }

  // unfortunately, I can't persist Transactions in Hibernate :(  only Lists, ArrayList and other crap.
  private Transactions transactions() {
    return new Transactions(transactions);
  }

  public Transaction withdraw($ amount) {
    new EnforceOpen();

    var transaction = new Transaction(WITHDRAWAL, amount);
    transactions.add(transaction);

    new EnforcePositiveBalance();
    new EnforceWithdrawalLimits();
    return transaction;
  }

  public Transaction deposit($ amount) {
    new EnforceOpen();
    var transaction = new Transaction(DEPOSIT, amount);
    transactions.add(transaction);
    return transaction;
  }

  public $ balance() {
    return transactions().balance();
  }

  public BankStatement statement(LocalDate from, LocalDate to) {
    return new BankStatement(from, to, transactions());
  }

  public void close(UnsatisfiedObligations unsatisfiedObligations) {
    checkState(!unsatisfiedObligations.exist(), "Bank account cannot be closed because a holder has unsatified obligations");
    status = Status.CLOSED;
  }

  public Long id() {
    return requireNonNull(id, "ID is null");
  }

  private class EnforceOpen {
    private EnforceOpen() {
      checkState(isOpen(), "Account is closed.");
    }

    private boolean isOpen() {
      return status.equals(Status.OPEN);
    }
  }

  private class EnforcePositiveBalance {
    private EnforcePositiveBalance() {
      checkState(isPositiveBalance(), "Not enough funds available on your account.");
    }

    private boolean isPositiveBalance() {
      return transactions().balance().isGe(ZERO);
    }
  }

  private class EnforceWithdrawalLimits {
    private EnforceWithdrawalLimits() {
      var withdrawnToday = totalWithdrawn(now(clock()));
      var dailyLimit = withdrawalLimit.dailyLimit();
      var withinDailyLimit = dailyLimit.isGe(withdrawnToday);
      checkState(withinDailyLimit, "Daily withdrawal limit (%s) reached.", dailyLimit);
    }

    private $ totalWithdrawn(LocalDate someDay) {
      return transactions().thatAre(
          and(
              tx -> tx.type() == WITHDRAWAL,
              tx -> tx.bookingDate().isEqual(someDay)))
          .balance()
          .abs();
    }

  }
}

