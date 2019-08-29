package awsm.domain.banking;

import static awsm.domain.offers.DecimalNumber.ZERO;
import static awsm.infra.time.TimeMachine.clock;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.base.Predicates.and;
import static java.time.LocalDate.now;
import static java.util.Objects.requireNonNull;

import awsm.domain.banking.BankStatement.Balance;
import awsm.domain.banking.BankStatement.Tx;
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
import javax.persistence.Version;
import org.threeten.extra.LocalDateRange;

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

  private Transactions transactions() {
    return new Transactions(this.transactions);
  }

  public Transaction withdraw(DecimalNumber amount) {
    checkState(!isClosed(), "Cannot withdraw funds from closed account.");
    var tx = transactions().withdrawal(amount);
    ensurePositiveBalance();
    ensureWithdrawalLimitIsNotExceeded();
    return tx;
  }

  public Transaction deposit(DecimalNumber amount) {
    checkState(!isClosed(), "Cannot deposit funds to closed account.");
    return transactions().deposit(amount);
  }

  private boolean isClosed() {
    return status.equals(Status.CLOSED);
  }

  private void ensurePositiveBalance() {
    checkState(transactions().sum().isEqualOrGreaterThan(ZERO),
        "Cannot withdraw more funds than available on your account.");
  }

  private void ensureWithdrawalLimitIsNotExceeded() {
    var withdrawnToday = totalWithdrawn(now(clock()));
    var dailyLimit = withdrawalLimit.dailyLimit();
    var withinDailyLimit = dailyLimit.isEqualOrGreaterThan(withdrawnToday);
    checkState(withinDailyLimit,
        "Cannot withdraw funds. Daily withdrawal limit (%s) reached.", dailyLimit);
  }

  private DecimalNumber totalWithdrawn(LocalDate someDay) {
    return transactions().sumIf(
        and(
            tx -> tx.isWithdrawal(),
            tx -> tx.isBooked(someDay)))
        .abs();
  }

  public DecimalNumber balance() {
    return transactions().sum();
  }

  public BankStatement statement(LocalDate from, LocalDate to) {

    var transactionsForStatement = new ArrayList<Tx>();

    var startingBalance = transactions().within(LocalDateRange.ofUnboundedStart(from)).sum();

    var closingBalance = transactions()
        .within(LocalDateRange.ofClosed(from, to))
        .stream()
        .foldLeft(startingBalance, (balance, tx) -> {
          var runningBalance = tx.apply(balance);
          transactionsForStatement.add(new Tx(
              tx.bookingTime(),
              tx.amount().withdrawal(),
              tx.amount().deposit(),
              runningBalance));
          return runningBalance;
        });

    return new BankStatement(
        new Balance(from, startingBalance),
        new Balance(to, closingBalance),
        transactionsForStatement
    );
  }

  public void close() {
    status = Status.CLOSED;
  }

  public Long id() {
    return requireNonNull(id, "ID is null");
  }
}