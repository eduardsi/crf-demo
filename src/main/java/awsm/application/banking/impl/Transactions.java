package awsm.application.banking.impl;

import static awsm.application.trading.impl.$.$;
import static awsm.application.trading.impl.$.ZERO;
import static awsm.infrastructure.time.TimeMachine.clock;

import awsm.application.trading.impl.$;
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

  $ balance() {
    return balance($.ZERO, (balance, tx) -> {});
  }

  $ balance($ seed, BiConsumer<$, Tx> consumer) {
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
        $ apply($ amount, $ balance) {
          return balance.add(amount);
        }
      },

      WITHDRAWAL {
        @Override
        $ apply($ amount, $ balance) {
          return balance.subtract(amount);
        }
      };

      abstract $ apply($ amount, $ balance);

      @Override
      public String toString() {
        return name().toLowerCase();
      }
    }

    private final $ amount;

    private final LocalDateTime bookingTime;

    private final Type type;

    Tx(BankAccountTxRecord jooqTx) {
      this(
          Type.valueOf(jooqTx.getType()),
          $(jooqTx.getAmount()),
          jooqTx.getBookingTime()
      );
    }

    Tx(Type type, $ amount, LocalDateTime bookingTime) {
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

    $ apply($ balance) {
      return type.apply(amount, balance);
    }

    $ withdrawn() {
      return testIf(isWithdrawal()) ? amount : ZERO;
    }

    $ deposited() {
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

    static Tx withdrawalOf($ amount) {
      return new Tx(Tx.Type.WITHDRAWAL, amount, LocalDateTime.now(clock()));
    }

    static Tx depositOf($ amount) {
      return new Tx(Tx.Type.DEPOSIT, amount, LocalDateTime.now(clock()));
    }

    static Function<Tx, BankAccountTxRecord> recordOfAccount(long accountId) {
      return it -> new BankAccountTxRecord()
          .setBankAccountId(accountId)
          .setAmount(it.amount.big())
          .setBookingTime(it.bookingTime)
          .setType(it.type.name());
    }


  }
}
