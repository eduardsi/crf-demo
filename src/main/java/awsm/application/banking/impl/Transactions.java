package awsm.application.banking.impl;

import static awsm.infrastructure.modeling.Amount.ZERO;
import static awsm.infrastructure.time.TimeMachine.clock;

import awsm.infrastructure.modeling.Amount;
import awsm.infrastructure.modeling.DomainEntity;
import com.google.common.collect.ImmutableList;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;
import jooq.tables.records.BankAccountTxRecord;
import one.util.streamex.StreamEx;
import org.threeten.extra.LocalDateRange;

class Transactions {

  private final ImmutableList<Tx> transactions;

  Transactions(List<Tx> transactions) {
    this.transactions = ImmutableList.copyOf(transactions);
  }

  Transactions thatAre(Predicate<Tx> condition) {
    return new Transactions(stream().filter(condition).toList());
  }

  Amount balance() {
    return balance(Amount.ZERO, (balance, tx) -> {});
  }

  Amount balance(Amount seed, BiConsumer<Amount, Tx> consumer) {
    return stream().foldLeft(seed, (balance, transaction) -> {
      var newBalance = transaction.apply(balance);
      consumer.accept(newBalance, transaction);
      return newBalance;
    });
  }

  Transactions with(Tx tx) {
    return new Transactions(ImmutableList.<Tx>builder()
        .addAll(transactions)
        .add(tx)
        .build());
  }


  StreamEx<Tx> stream() {
    return StreamEx.of(transactions);
  }

  static Transactions none() {
    return new Transactions(ImmutableList.of());
  }

  static class Tx implements DomainEntity<Tx> {

    public enum Type {
      DEPOSIT {
        @Override
        Amount apply(Amount amount, Amount balance) {
          return balance.add(amount);
        }
      },

      WITHDRAWAL {
        @Override
        Amount apply(Amount amount, Amount balance) {
          return balance.subtract(amount);
        }
      };

      abstract Amount apply(Amount amount, Amount balance);

      @Override
      public String toString() {
        return name().toLowerCase();
      }
    }

    private final Amount amount;

    private final LocalDateTime bookingTime;

    private final Type type;

    Tx(BankAccountTxRecord jooqTx) {
      this(
          Type.valueOf(jooqTx.getType()),
          Amount.of(jooqTx.getAmount()),
          jooqTx.getBookingTime()
      );
    }

    Tx(Type type, Amount amount, LocalDateTime bookingTime) {
      this.type = type;
      this.amount = amount;
      this.bookingTime = bookingTime;
    }

    LocalDateTime bookingTime() {
      return bookingTime;
    }

    private LocalDate bookingDate() {
      return bookingTime.toLocalDate();
    }

    Amount apply(Amount balance) {
      return type.apply(amount, balance);
    }

    Amount withdrawn() {
      return testIf(isWithdrawal()) ? amount : ZERO;
    }

    Amount deposited() {
      return testIf(isDeposit()) ? amount : ZERO;
    }

    static Predicate<Tx> isWithdrawal() {
      return tx -> tx.type == Type.WITHDRAWAL;
    }

    private static Predicate<Tx> isDeposit() {
      return tx -> tx.type == Type.DEPOSIT;
    }

    static Predicate<Tx> bookedOn(LocalDate date) {
      return tx -> tx.bookingDate().isEqual(date);
    }

    static Predicate<Tx> bookedBefore(LocalDate date) {
      return tx -> LocalDateRange.ofUnboundedStart(date).contains(tx.bookingDate());
    }

    static Predicate<Tx> bookedDuring(LocalDate from, LocalDate to) {
      return tx -> LocalDateRange.ofClosed(from, to).contains(tx.bookingDate());
    }

    static Tx withdrawalOf(Amount amount) {
      return new Tx(Tx.Type.WITHDRAWAL, amount, LocalDateTime.now(clock()));
    }

    static Tx depositOf(Amount amount) {
      return new Tx(Tx.Type.DEPOSIT, amount, LocalDateTime.now(clock()));
    }

    static Function<Tx, BankAccountTxRecord> recordOfAccount(long accountId) {
      return it -> new BankAccountTxRecord()
          .setBankAccountId(accountId)
          .setAmount(it.amount.toBigDecimal())
          .setBookingTime(it.bookingTime)
          .setType(it.type.name());
    }


  }
}
