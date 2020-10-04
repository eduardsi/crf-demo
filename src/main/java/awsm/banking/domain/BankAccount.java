package awsm.banking.domain;

import com.github.javafaker.Faker;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.Collection;

import static awsm.banking.domain.Transaction.depositOf;
import static awsm.infrastructure.clock.TimeMachine.today;
import static com.google.common.base.Preconditions.checkState;

@Entity
public class BankAccount implements DomainEntity {

  transient DomainEvents events = DomainEvents.defaultInstance();


  enum Status {
    NEW, OPEN, CLOSED
  }

  @Id
  private String iban;

  @Enumerated(EnumType.STRING)
  private Status status = Status.NEW;

  @Embedded
  private WithdrawalLimits withdrawalLimits;

  @Embedded
  private AccountHolder holder;

  @ElementCollection
  @CollectionTable(name = "BANK_ACCOUNT_TX")
  @OrderColumn(name = "INDEX")
  private Collection<Transaction> transactions = new ArrayList<>();

  @Version
  private long version;

  public BankAccount(AccountHolder holder, WithdrawalLimits withdrawalLimits) {
    this.withdrawalLimits = withdrawalLimits;
    this.holder = holder;
    this.iban = new Faker().finance().iban();
  }

  private BankAccount() {
  }

  AccountHolder holder() {
    return holder;
  }

  String iban() {
    return iban;
  }

  public void open() {
    this.status = Status.OPEN;
    events.publish(new BankAccountOpened(iban));
  }

  public Transaction withdraw(BigDecimal amount) {
    new EnforceOpen();

    var tx = Transaction.withdrawalOf(amount);
    transactions.add(tx);

    new EnforcePositiveBalance();
    new EnforceMonthlyWithdrawalLimit();
    new EnforceDailyWithdrawalLimit();

    return tx;
  }

  public Transaction deposit(BigDecimal amount) {
    new EnforceOpen();

    var tx = depositOf(amount);
    transactions.add(tx);

    return tx;
  }

  public BigDecimal balance() {
    return new Balance(transactions.stream()).abs();
  }

  public void close(UnsatisfiedObligations unsatisfiedObligations) {
    checkState(!unsatisfiedObligations.exist(), "Bank account cannot be closed because a holder has unsatisfied obligations");
    status = Status.CLOSED;
  }

  private class EnforceOpen {
    private EnforceOpen() {
      checkState(isOpen(), "Account is not open.");
    }
  }

  public boolean isOpen() {
    return status.equals(Status.OPEN);
  }

  public boolean isClosed() {
    return status.equals(Status.CLOSED);
  }

  private class EnforcePositiveBalance {


    private EnforcePositiveBalance() {
      var balance = new Balance(transactions.stream());
      checkState(balance.isPositive(), "Not enough funds available on your account.");
    }
  }

  private class EnforceDailyWithdrawalLimit {

    private EnforceDailyWithdrawalLimit() {
      var dailyLimit = withdrawalLimits.dailyLimit();
      var dailyLimitReached = withdrawn(today()).compareTo(dailyLimit) > 0;
      checkState(!dailyLimitReached, "Daily withdrawal limit (%s) reached.", dailyLimit);
    }

    private BigDecimal withdrawn(LocalDate someDay) {
      var balance = new Balance(transactions
              .stream()
              .filter(tx -> tx.bookedIn(someDay))
              .filter(tx -> tx.isWithdrawal()));
      return balance.abs();
    }
  }

  private class EnforceMonthlyWithdrawalLimit {

    private EnforceMonthlyWithdrawalLimit() {
      var thisMonth = today().getMonth();
      var monthlyLimit = withdrawalLimits.monthlyLimit();
      var monthlyLimitReached = withdrawn(thisMonth).compareTo(monthlyLimit) > 0;
      checkState(!monthlyLimitReached, "Monthly withdrawal limit (%s) reached.", monthlyLimit);
    }

    private BigDecimal withdrawn(Month month) {
      Balance balance = new Balance(transactions
              .stream()
              .filter(tx -> tx.bookedIn(month))
              .filter(tx -> tx.isWithdrawal())
      );
      return balance.abs();
    }
  }

}

