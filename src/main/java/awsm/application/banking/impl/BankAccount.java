package awsm.application.banking.impl;

import static awsm.infrastructure.modeling.Amount.ZERO;
import static awsm.infrastructure.time.TimeMachine.today;
import static com.google.common.base.Preconditions.checkState;
import static java.util.Objects.requireNonNull;
import static jooq.tables.BankAccount.BANK_ACCOUNT;

import awsm.application.banking.impl.Transactions.Transaction;
import awsm.infrastructure.modeling.Amount;
import java.time.LocalDate;
import java.util.Optional;
import java.util.function.Function;
import jooq.tables.records.BankAccountRecord;
import org.jooq.DSLContext;
import org.springframework.stereotype.Component;

public class BankAccount {

  enum Status {
    OPEN, CLOSED
  }

  public enum Type {
    CHECKING, SAVINGS
  }

  private Status status = Status.OPEN;

  private final Type type;

  private final WithdrawalLimit withdrawalLimit;

  private final Iban iban;

  private Transactions committedTransactions = Transactions.none();

  private Optional<Long> id = Optional.empty();

  public BankAccount(Type type, Iban iban, WithdrawalLimit withdrawalLimit) {
    this.iban = iban;
    this.type = type;
    this.withdrawalLimit = requireNonNull(withdrawalLimit, "Withdrawal limit is mandatory");
  }

  public long id() {
    return id.orElseThrow();
  }

  public Transaction withdraw(Amount amount) {
    new EnforceOpen();

    var tx = Transaction.withdrawalOf(amount);
    var uncommittedTransactions = committedTransactions.with(tx);

    new EnforcePositiveBalance(uncommittedTransactions);
    new EnforceWithdrawalLimits(uncommittedTransactions);

    committedTransactions = uncommittedTransactions;

    return tx;
  }

  public Transaction deposit(Amount amount) {
    new EnforceOpen();

    var tx = Transaction.depositOf(amount);

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
      return transactions.balance().isAtLeast(ZERO);
    }
  }

  private class EnforceWithdrawalLimits {

    private final Transactions transactions;

    private EnforceWithdrawalLimits(Transactions transactions) {
      this.transactions = transactions;
      var dailyLimit = withdrawalLimit.dailyLimit();
      var notExceeded = withdrawn(today()).isAtMost(dailyLimit);
      checkState(notExceeded, "Daily withdrawal limit (%s) reached.", dailyLimit);
    }

    private Amount withdrawn(LocalDate someDay) {
      return transactions
          .thatAre(tx -> tx.bookedOn(someDay))
          .thatAre(tx -> tx.isWithdrawal())
          .balance()
          .abs();
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
          .where(BANK_ACCOUNT.ID.equal(self.id.orElseThrow()))
          .execute();
      transactionsRepository.delete(self);
      transactionsRepository.insert(self, self.committedTransactions);
    }

    private void insert(BankAccount self) {
      var id = dsl
              .insertInto(BANK_ACCOUNT)
              .set(toJooq(self))
              .returning(BANK_ACCOUNT.ID)
              .fetchOne()
              .getId();
      self.id = Optional.of(id);
      transactionsRepository.insert(self, self.committedTransactions);
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
            Type.valueOf(jooq.getType()),
            new Iban(jooq.getIban()),
            new WithdrawalLimit(Amount.of(jooq.getDailyLimit())));
        self.id = Optional.of(jooq.getId());
        self.status = Status.valueOf(jooq.getStatus());
        self.committedTransactions = transactionsRepository.list(self);
        return self;
      };
    }

    private BankAccountRecord toJooq(BankAccount self) {
      return new BankAccountRecord()
          .setIban(self.iban + "")
          .setStatus(self.status.name())
          .setType(self.type.name())
          .setDailyLimit(self.withdrawalLimit.dailyLimit().toBigDecimal());
    }

  }

}

