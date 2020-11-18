package awsm.domain.banking;

import awsm.domain.core.AggregateRoot;
import awsm.domain.core.Amount;
import com.github.javafaker.Faker;
import one.util.streamex.StreamEx;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static awsm.domain.banking.Transaction.depositOf;
import static awsm.infrastructure.clock.TimeMachine.today;
import static com.google.common.base.Preconditions.checkState;

@Entity
public class BankAccount extends AggregateRoot<BankAccount> {

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
  private List<Transaction> transactions = new ArrayList<>();

  @Version
  private long version;

  public BankAccount(AccountHolder holder, WithdrawalLimits withdrawalLimits) {
    this.withdrawalLimits = withdrawalLimits;
    this.holder = holder;
    this.iban = new Faker().finance().iban("LV");
  }

  BankAccount() {
  }

  public AccountHolder holder() {
    return holder;
  }

  public String iban() {
    return iban;
  }

  public void open() {
    this.status = Status.OPEN;
    publish(new BankAccountOpened(iban));
  }

  public Transaction tx(UUID uid) {
    return transactions.stream().filter(tx -> tx.uid().equals(uid)).findAny().orElseThrow();
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
    checkState(!unsatisfiedObligations.exist(), "Bank account cannot be closed because a holder has unsatisfied obligations");
    status = Status.CLOSED;
  }

  public boolean isOpen() {
    return status.equals(Status.OPEN);
  }

  public boolean isClosed() {
    return status.equals(Status.CLOSED);
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

