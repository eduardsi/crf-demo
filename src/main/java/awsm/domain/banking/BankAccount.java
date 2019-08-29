package awsm.domain.banking;

import static awsm.domain.offers.DecimalNumber.ZERO;
import static awsm.infra.time.TimeMachine.clock;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.base.Predicates.and;
import static java.time.LocalDate.now;
import static java.util.Objects.requireNonNull;

import awsm.domain.offers.DecimalNumber;
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
import javax.persistence.Transient;
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
  private List<Transaction> tx = new ArrayList<>();

  @Transient
  private transient Transactions transactions = new Transactions(tx);

  @Version
  private long version;

  public BankAccount(WithdrawalLimit withdrawalLimit) {
    this.withdrawalLimit = requireNonNull(withdrawalLimit, "Withdrawal limit is mandatory");
  }

  @HibernateConstructor
  private BankAccount() {
  }

  public Transaction withdraw(DecimalNumber amount) {
    new IsOpen().enforce();
    var tx = transactions.withdrawal(amount);
    new BalanceIsPositive().enforce();
    new WithdrawalLimitNotExceeded().enforce();
    return tx;
  }

  public Transaction deposit(DecimalNumber amount) {
    new IsOpen().enforce();
    return transactions.deposit(amount);
  }

  public DecimalNumber balance() {
    return transactions.sum();
  }

  public BankStatement statement(LocalDate from, LocalDate to) {
    return new BankStatement(from, to, transactions);
  }

  public void close() {
    status = Status.CLOSED;
  }

  public Long id() {
    return requireNonNull(id, "ID is null");
  }

  private class IsOpen {
    void enforce() {
      checkState(isOpen(), "Account is closed.");
    }

    private boolean isOpen() {
      return status.equals(Status.OPEN);
    }
  }

  private class BalanceIsPositive {
    void enforce() {
      var balanceIsPositive = transactions.sum().isEqualOrGreaterThan(ZERO);
      checkState(balanceIsPositive, "Not enough funds available on your account.");
    }
  }

  private class WithdrawalLimitNotExceeded {
    void enforce() {
      var withdrawnToday = totalWithdrawn(now(clock()));
      var dailyLimit = withdrawalLimit.dailyLimit();
      var withinDailyLimit = dailyLimit.isEqualOrGreaterThan(withdrawnToday);
      checkState(withinDailyLimit, "Daily withdrawal limit (%s) reached.", dailyLimit);
    }

    private DecimalNumber totalWithdrawn(LocalDate someDay) {
      return transactions.sumIf(
          and(
              tx -> tx.isWithdrawal(),
              tx -> tx.isBooked(someDay)))
          .abs();
    }
  }

}