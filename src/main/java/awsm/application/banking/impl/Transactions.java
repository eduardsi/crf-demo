package awsm.application.banking.impl;

import static awsm.infrastructure.modeling.Amount.ZERO;
import static awsm.infrastructure.time.TimeMachine.clock;
import static java.util.stream.Collectors.toList;
import static jooq.tables.BankAccountTx.BANK_ACCOUNT_TX;

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
import org.jooq.DSLContext;
import org.springframework.stereotype.Component;
import org.threeten.extra.LocalDateRange;

class Transactions {

  private final ImmutableList<Tx> transactions;

  private Transactions(List<Tx> transactions) {
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


  private StreamEx<Tx> stream() {
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

    private final LocalDate bookingDate;

    private final Type type;

    Tx(Type type, Amount amount, LocalDateTime bookingTime) {
      this.type = type;
      this.amount = amount;
      this.bookingTime = bookingTime;
      this.bookingDate = bookingTime.toLocalDate();
    }

    LocalDateTime bookingTime() {
      return bookingTime;
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
      return tx -> tx.bookingDate.isEqual(date);
    }

    static Predicate<Tx> bookedBefore(LocalDate date) {
      return tx -> LocalDateRange.ofUnboundedStart(date).contains(tx.bookingDate);
    }

    static Predicate<Tx> bookedDuring(LocalDate from, LocalDate to) {
      return tx -> LocalDateRange.ofClosed(from, to).contains(tx.bookingDate);
    }

    static Tx withdrawalOf(Amount amount) {
      return new Tx(Tx.Type.WITHDRAWAL, amount, LocalDateTime.now(clock()));
    }

    static Tx depositOf(Amount amount) {
      return new Tx(Tx.Type.DEPOSIT, amount, LocalDateTime.now(clock()));
    }

  }

  @Component
  static class Repository {

    private final DSLContext dsl;

    Repository(DSLContext dsl) {
      this.dsl = dsl;
    }

    void insert(BankAccount bankAccount, Transactions self) {
      self.stream()
          .forEach(tx -> dsl
              .insertInto(BANK_ACCOUNT_TX)
              .set(BANK_ACCOUNT_TX.BANK_ACCOUNT_ID, bankAccount.id())
              .set(BANK_ACCOUNT_TX.AMOUNT, tx.amount.toBigDecimal())
              .set(BANK_ACCOUNT_TX.BOOKING_TIME, tx.bookingTime)
              .set(BANK_ACCOUNT_TX.TYPE, tx.type.name())
              .execute());
    }

    void delete(BankAccount bankAccount) {
      dsl
          .deleteFrom(BANK_ACCOUNT_TX)
          .where(BANK_ACCOUNT_TX.BANK_ACCOUNT_ID.eq(bankAccount.id()))
          .execute();
    }

    Transactions list(BankAccount bankAccount) {
      return new Transactions(dsl
          .selectFrom(BANK_ACCOUNT_TX)
          .where(BANK_ACCOUNT_TX.BANK_ACCOUNT_ID.equal(bankAccount.id()))
          .orderBy(BANK_ACCOUNT_TX.INDEX.asc())
          .fetchStream()
          .map(fromJooq())
          .collect(toList()));
    }

    private Function<BankAccountTxRecord, Tx> fromJooq() {
      return jooq -> new Tx(
          Tx.Type.valueOf(jooq.getType()),
          Amount.of(jooq.getAmount()),
          jooq.getBookingTime()
      );
    }

  }
}
