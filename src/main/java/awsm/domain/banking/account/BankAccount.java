package awsm.domain.banking.account;

import static awsm.domain.banking.account.Transactions.Transaction.depositOf;
import static awsm.infrastructure.time.TimeMachine.today;
import static com.google.common.base.Preconditions.checkState;
import static jooq.tables.BankAccount.BANK_ACCOUNT;

import java.time.LocalDate;
import java.time.Month;
import java.util.Optional;
import java.util.function.Function;

import awsm.domain.banking.*;
import awsm.domain.banking.commons.Amount;
import awsm.domain.banking.customer.Customer;
import jooq.tables.records.BankAccountRecord;
import org.jooq.DSLContext;

public class BankAccount {

  enum Status {
    OPEN, CLOSED
  }

  private Status status = Status.OPEN;

  private final WithdrawalLimits withdrawalLimits;

  private final Iban iban;

  private Transactions committedTransactions = new Transactions(this);

  private Optional<Long> id = Optional.empty();

  private Optional<Long> holderId = Optional.empty();

  public BankAccount(WithdrawalLimits withdrawalLimits) {
    this(new Iban(), withdrawalLimits);
  }

  private BankAccount(Iban iban, WithdrawalLimits withdrawalLimits) {
    this.iban = iban;
    this.withdrawalLimits = withdrawalLimits;
  }

  void claim(Customer customer) {
    this.holderId = Optional.of(customer.id());
  }

  public long id() {
    return id.orElseThrow();
  }

  public Transactions.Transaction withdraw(Amount amount) {
    new EnforceOpen();

    var tx = Transactions.Transaction.withdrawalOf(amount);
    var uncommittedTransactions = committedTransactions.with(tx);

    new EnforcePositiveBalance(uncommittedTransactions);
    new EnforceMonthlyWithdrawalLimit(uncommittedTransactions);
    new EnforceDailyWithdrawalLimit(uncommittedTransactions);

    committedTransactions = uncommittedTransactions;

    return tx;
  }

  public Transactions.Transaction deposit(Amount amount) {
    new EnforceOpen();

    var tx = depositOf(amount);

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

  public void open(DSLContext dsl) {
    var repo = new Repo(dsl);
    repo.insert(this);
  }

  public void update(Repo repo) {
    repo.update(this);
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
      return transactions.balance().isGreaterThanOrEqualTo(Amount.amount("0.00"));
    }
  }

  private class EnforceDailyWithdrawalLimit {

    private final Transactions transactions;

    private EnforceDailyWithdrawalLimit(Transactions transactions) {
      this.transactions = transactions;
      var dailyLimit = withdrawalLimits.dailyLimit();
      var notExceeded = withdrawn(today()).isLessThanOrEqualTo(dailyLimit);
      checkState(notExceeded, "Daily withdrawal limit (%s) reached.", dailyLimit);
    }

    private Amount withdrawn(LocalDate someDay) {
      return transactions
          .thatAre(tx -> tx.bookedIn(someDay))
          .thatAre(tx -> tx.isWithdrawal())
          .balance()
          .abs();
    }
  }

  private class EnforceMonthlyWithdrawalLimit {

    private final Transactions transactions;

    private EnforceMonthlyWithdrawalLimit(Transactions transactions) {
      this.transactions = transactions;
      var monthlyLimit = withdrawalLimits.monthlyLimit();
      var thisMonth = today().getMonth();
      var notExceeded = withdrawn(thisMonth).isLessThanOrEqualTo(monthlyLimit);
      checkState(notExceeded, "Monthly withdrawal limit (%s) reached.", monthlyLimit);
    }

    private Amount withdrawn(Month month) {
      return transactions
          .thatAre(tx -> tx.bookedIn(month))
          .thatAre(tx -> tx.isWithdrawal())
          .balance()
          .abs();
    }
  }

  public static class Repo {

    private final DSLContext dsl;
    private final Transactions.Repo transactionsRepo;

    Repo(DSLContext dsl) {
      this.dsl = dsl;
      this.transactionsRepo = new Transactions.Repo(dsl);
    }

    private void insert(BankAccount self) {
      var id = dsl
              .insertInto(BANK_ACCOUNT)
              .set(toJooq(self))
              .returning(BANK_ACCOUNT.ID)
              .fetchOne()
              .getId();
      self.id = Optional.of(id);
      self.committedTransactions.insert(transactionsRepo);
    }

    private void update(BankAccount self) {
      dsl.update(BANK_ACCOUNT)
          .set(toJooq(self))
          .where(BANK_ACCOUNT.ID.equal(self.id()))
          .execute();
      self.committedTransactions.delete(transactionsRepo);
      self.committedTransactions.insert(transactionsRepo);
    }

    BankAccount singleBy(long id) {
      return dsl
          .selectFrom(BANK_ACCOUNT)
          .where(BANK_ACCOUNT.ID.equal(id))
          .fetchOptional()
          .map(fromJooq())
          .orElseThrow();
    }

    private Function<BankAccountRecord, BankAccount> fromJooq() {
      return jooq -> {
        var self = new BankAccount(
            new Iban(jooq.getIban()),
            new WithdrawalLimits(Amount.amount(jooq.getDailyLimit()), Amount.amount(jooq.getMonthlyLimit()))
        );
        self.id = Optional.of(jooq.getId());
        self.holderId = Optional.ofNullable(jooq.getHolderId());
        self.status = Status.valueOf(jooq.getStatus());
        self.committedTransactions = transactionsRepo.listBy(self);
        return self;
      };
    }

    private BankAccountRecord toJooq(BankAccount self) {
      return new BankAccountRecord()
          .setIban(self.iban + "")
          .setHolderId(self.holderId.orElse(null))
          .setStatus(self.status.name())
          .setMonthlyLimit(self.withdrawalLimits.monthlyLimit().decimal())
          .setDailyLimit(self.withdrawalLimits.dailyLimit().decimal());
    }

  }

}

