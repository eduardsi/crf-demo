package dagger_games;

import com.github.javafaker.Faker;
import com.google.common.collect.ForwardingSortedSet;
import jooq.Keys;
import jooq.tables.records.BankAccountRecord;
import one.util.streamex.StreamEx;
import org.jooq.DSLContext;

import java.time.LocalDate;
import java.util.SortedSet;
import java.util.TreeSet;

import static awsm.infrastructure.clock.TimeMachine.clock;
import static awsm.infrastructure.clock.TimeMachine.today;
import static com.google.common.base.Preconditions.checkState;
import static dagger_games.TransactionType.WITHDRAWAL;
import static java.time.LocalDateTime.now;
import static java.util.stream.Collectors.toCollection;

public class BankAccount {

  public enum Status {
    OPEN, CLOSED
  }

  private final SortedSet<Transaction> transactions;
  private final BankAccountRecord self;

  public BankAccount(BankAccountHolder accountHolder, WithdrawalLimits withdrawalLimits) {
    this.self = new BankAccountRecord()
            .setIban(new Faker().finance().iban())
            .setStatus(Status.OPEN)
            .setDailyLimit(withdrawalLimits.dailyLimit)
            .setMonthlyLimit(withdrawalLimits.monthlyLimit)
            .setFirstName(accountHolder.firstName)
            .setLastName(accountHolder.lastName)
            .setEmail(accountHolder.email)
            .setPersonalId(accountHolder.personalId);

    this.transactions = new TreeSet<>();
  }

  BankAccount(BankAccountRecord self) {
    this.self = self;

    // lazy loading
    this.transactions = new ForwardingSortedSet<>() {
      @Override
      protected SortedSet<Transaction> delegate() {
        return self
                .fetchChildren(Keys.BANK_ACCOUNT_TX__FK_IBAN)
                .stream()
                .map(Transaction::new)
                .collect(toCollection(TreeSet::new));
      }
    };
  }

  String iban() {
    return self.getIban();
  }

  public Transaction withdraw(Amount amount) {
    new EnforceOpen();

    var tx = new Transaction(self.getIban(), WITHDRAWAL, amount, now(clock()));
    transactions.add(tx);

    new EnforcePositiveBalance();
    new EnforceWithdrawalLimits();

    return tx;
  }

  public Transaction deposit(Amount amount) {
    new EnforceOpen();

    var tx = new Transaction(self.getIban(), TransactionType.DEPOSIT, amount, now(clock()));
    transactions.add(tx);

    return tx;
  }

  public Amount balance() {
    return StreamEx.of(transactions).foldRight(Amount.ZERO, Transaction::apply);
  }

  public void close(UnsatisfiedObligations unsatisfiedObligations) {
    checkState(!unsatisfiedObligations.exist(), "Bank account cannot be closed because a holder has unsatisfied obligations");
    self.setStatus(Status.CLOSED);
  }

  public void save(DSLContext dsl) {
    self.attach(dsl.configuration());
    self.store();
    transactions.forEach(tx -> tx.save(dsl));
  }

  public boolean isOpen() {
    return self.getStatus().equals(Status.OPEN);
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

  private class EnforceWithdrawalLimits {

    private EnforceWithdrawalLimits() {
      var dailyLimit = self.getDailyLimit();
      var dailyLimitReached = withdrawn(today()).isGreaterThan(dailyLimit);
      checkState(!dailyLimitReached, "Daily withdrawal limit (%s) reached.", dailyLimit);
    }

    private Amount withdrawn(LocalDate someDay) {
      return StreamEx.of(transactions)
              .filter(tx -> tx.bookedOn(someDay))
              .filter(tx -> tx.isWithdrawal())
              .foldRight(Amount.ZERO, Transaction::apply)
              .abs();
    }
  }

}