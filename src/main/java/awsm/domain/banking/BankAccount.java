package awsm.domain.banking;

import static awsm.domain.banking.BankAccount.Status.*;
import static awsm.domain.banking.Transaction.depositOf;
import static awsm.infrastructure.clock.TimeMachine.today;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;

import awsm.domain.core.AggregateRoot;
import awsm.domain.core.Amount;
import com.github.javafaker.Faker;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.*;
import one.util.streamex.StreamEx;

@Entity
public class BankAccount extends AggregateRoot<BankAccount> {

  enum Status {
    NEW,
    OPEN,
    SUSPENDED,
    CLOSED
  }

  @Id private String iban;

  @Enumerated(EnumType.STRING)
  private Status status = Status.NEW;

  @Embedded private WithdrawalLimits withdrawalLimits;

  @Embedded private AccountHolder holder;

  @ElementCollection
  @CollectionTable(name = "BANK_ACCOUNT_TX", joinColumns = @JoinColumn(name = "BANK_ACCOUNT_IBAN"))
  @OrderColumn(name = "INDEX")
  private List<Transaction> transactions = new ArrayList<>();

  public BankAccount(AccountHolder holder, WithdrawalLimits withdrawalLimits) {
    this.withdrawalLimits = withdrawalLimits;
    this.holder = holder;
    this.iban = new Faker().finance().iban("LV");
  }

  BankAccount() {}

  public AccountHolder holder() {
    return holder;
  }

  public String iban() {
    return iban;
  }

  public void open() {
    this.status = OPEN;
    publish(new BankAccountOpened(iban, today()));
  }

  public void suspend() {
    checkState(!this.status.equals(SUSPENDED), "Bank account is already suspended");
    this.status = SUSPENDED;
  }

  public Transaction tx(String uid) {
    return transactions.stream().filter(tx -> tx.uid().equals(uid)).findAny().orElseThrow();
  }

  public void lift(WithdrawalLimits newLimits) {
    checkArgument(
        newLimits.dailyLimit().isGreaterThanOrEqualTo(withdrawalLimits.dailyLimit()),
        "New daily limit cannot be less than the current limit");
    checkArgument(
        newLimits.monthlyLimit().isGreaterThanOrEqualTo(withdrawalLimits.monthlyLimit()),
        "New daily limit cannot be less than the current limit");
    this.withdrawalLimits = newLimits;
  }

  public Transaction withdraw(Amount amount) {
    new EnforceOpen();

    var tx = Transaction.withdrawalOf(amount);
    transactions.add(tx);

    new EnforcePositiveBalance();
    new EnforceMonthlyWithdrawalLimit();
    new EnforceDailyWithdrawalLimit();

    publish(new WithdrawalHappened(iban, tx.uid()));

    return tx;
  }

  public Transaction deposit(Amount amount) {
    new EnforceOpen();

    var tx = depositOf(amount);
    transactions.add(tx);

    return tx;
  }

  public BankStatement statement(LocalDate fromInclusive, LocalDate toInclusive) {
    return new BankStatement(fromInclusive, toInclusive, transactions);
  }

  public Amount balance() {
    return StreamEx.of(transactions).foldRight(Amount.ZERO, Transaction::apply);
  }

  public void close(UnsatisfiedObligations unsatisfiedObligations) {
    checkState(
        !unsatisfiedObligations.exist(),
        "Bank account cannot be closed because a holder has unsatisfied obligations");
    status = CLOSED;
  }

  public boolean isOpen() {
    return status.equals(OPEN);
  }

  public boolean isClosed() {
    return status.equals(CLOSED);
  }

  public boolean isSuspended() {
    return status.equals(SUSPENDED);
  }

  WithdrawalLimits withdrawalLimits() {
    return withdrawalLimits;
  }

  private class EnforceOpen {
    private EnforceOpen() {
      checkState(isOpen(), "Account is not open.");
    }
  }

  private class EnforcePositiveBalance {

    private EnforcePositiveBalance() {
      checkState(balance().isPositive(), "Not enough funds available on your account.");
    }
  }

  private class EnforceDailyWithdrawalLimit {

    private EnforceDailyWithdrawalLimit() {
      var dailyLimit = withdrawalLimits.dailyLimit();
      var dailyLimitReached = withdrawn(today()).isGreaterThan(dailyLimit);
      checkState(!dailyLimitReached, "Daily withdrawal limit (%s) reached.", dailyLimit);
    }

    private Amount withdrawn(LocalDate someDay) {
      return StreamEx.of(transactions)
          .filter(tx -> tx.bookedIn(someDay))
          .filter(tx -> tx.isWithdrawal())
          .foldRight(Amount.ZERO, Transaction::apply)
          .abs();
    }
  }

  private class EnforceMonthlyWithdrawalLimit {

    private EnforceMonthlyWithdrawalLimit() {
      var thisMonth = today().getMonth();
      var monthlyLimit = withdrawalLimits.monthlyLimit();
      var monthlyLimitReached = withdrawn(thisMonth).isGreaterThan(monthlyLimit);
      checkState(!monthlyLimitReached, "Monthly withdrawal limit (%s) reached.", monthlyLimit);
    }

    private Amount withdrawn(Month month) {
      return StreamEx.of(transactions)
          .filter(tx -> tx.bookedIn(month))
          .filter(tx -> tx.isWithdrawal())
          .foldRight(Amount.ZERO, Transaction::apply)
          .abs();
    }
  }
}
