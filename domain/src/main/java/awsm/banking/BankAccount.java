package awsm.banking;

import static awsm.infrastructure.time.TimeMachine.today;
import static com.google.common.base.Preconditions.checkState;
import static jooq.tables.BankAccount.BANK_ACCOUNT;

import java.time.LocalDate;
import java.time.Month;
import java.util.function.Function;
import jooq.tables.records.BankAccountRecord;
import org.jooq.DSLContext;
import org.springframework.stereotype.Component;

class BankAccount {

  enum Status {
    OPEN, CLOSED
  }

  private Status status = Status.OPEN;

  private final WithdrawalLimits withdrawalLimits;

  private final Iban iban;

  private final Currency currency;

  private Transactions committedTransactions = new Transactions(this);

  private BankAccountId id = new BankAccountId();

  BankAccount(Currency currency, WithdrawalLimits withdrawalLimits) {
    this(new Iban(), currency, withdrawalLimits);
  }

  private BankAccount(Iban iban, Currency currency, WithdrawalLimits withdrawalLimits) {
    this.iban = iban;
    this.withdrawalLimits = withdrawalLimits;
    this.currency = currency;
  }

  public BankAccountId id() {
    return id;
  }

  public Transactions.Transaction withdraw(Money money) {
    new EnforceOpen();
    new EnforceAccountCurrency(money);

    var tx = Transactions.Transaction.withdrawalOf(money);
    var uncommittedTransactions = committedTransactions.with(tx);

    new EnforcePositiveBalance(uncommittedTransactions);
    new EnforceMonthlyWithdrawalLimit(uncommittedTransactions);
    new EnforceDailyWithdrawalLimit(uncommittedTransactions);

    committedTransactions = uncommittedTransactions;

    return tx;
  }

  public Transactions.Transaction deposit(Money money) {
    new EnforceOpen();
    new EnforceAccountCurrency(money);

    var tx = Transactions.Transaction.depositOf(money);

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

  public void saveNew(Repository repository) {
    repository.insert(this);
  }

  public void save(Repository repository) {
    repository.update(this);
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

  private class EnforceAccountCurrency {
    private EnforceAccountCurrency(Money money) {
      var currencyMatch = currency.equals(money.currency());
      checkState(currencyMatch, "Cannot perform %s operation on %s account.", money.currency(), currency);
    }
  }

  @Component
  static class Repository {

    private final DSLContext dsl;
    private final Transactions.Repository transactionsRepository;

    Repository(DSLContext dsl, Transactions.Repository transactionsRepository) {
      this.dsl = dsl;
      this.transactionsRepository = transactionsRepository;
    }

    private void update(BankAccount self) {
      dsl.update(BANK_ACCOUNT)
          .set(toJooq(self))
          .where(BANK_ACCOUNT.ID.equal(self.id.asLong()))
          .execute();
      self.committedTransactions.delete(transactionsRepository);
      self.committedTransactions.saveNew(transactionsRepository);
    }

    private void insert(BankAccount self) {
      var id = dsl
              .insertInto(BANK_ACCOUNT)
              .set(toJooq(self))
              .returning(BANK_ACCOUNT.ID)
              .fetchOne()
              .getId();
      self.id = new BankAccountId(id);
      self.committedTransactions.saveNew(transactionsRepository);
    }

    BankAccount singleBy(BankAccountId id) {
      return dsl
          .selectFrom(BANK_ACCOUNT)
          .where(BANK_ACCOUNT.ID.equal(id.asLong()))
          .fetchOptional()
          .map(fromJooq())
          .orElseThrow();
    }

    private Function<BankAccountRecord, BankAccount> fromJooq() {
      return jooq -> {
        var self = new BankAccount(
            new Iban(jooq.getIban()),
            new Currency(jooq.getCurrency()),
            new WithdrawalLimits(Amount.amount(jooq.getDailyLimit()), Amount.amount(jooq.getMonthlyLimit()))
        );
        self.id = new BankAccountId(jooq.getId());
        self.status = Status.valueOf(jooq.getStatus());
        self.committedTransactions = transactionsRepository.listBy(self);
        return self;
      };
    }

    private BankAccountRecord toJooq(BankAccount self) {
      return new BankAccountRecord()
          .setIban(self.iban + "")
          .setCurrency(self.currency + "")
          .setStatus(self.status.name())
          .setMonthlyLimit(self.withdrawalLimits.monthlyLimit().decimal())
          .setDailyLimit(self.withdrawalLimits.dailyLimit().decimal());
    }

  }

}

