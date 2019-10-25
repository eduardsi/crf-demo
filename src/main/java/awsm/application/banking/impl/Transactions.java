package awsm.application.banking.impl;

import static awsm.application.banking.impl.Transactions.Transaction.Type.DEPOSIT;
import static awsm.application.banking.impl.Transactions.Transaction.Type.WITHDRAWAL;
import static awsm.infrastructure.modeling.Amount.ZERO;
import static awsm.infrastructure.time.TimeMachine.clock;
import static java.util.stream.Collectors.toList;
import static jooq.tables.BankAccountTx.BANK_ACCOUNT_TX;

import awsm.infrastructure.modeling.Amount;
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

  private final ImmutableList<Transaction> transactions;

  private Transactions(List<Transaction> transactions) {
    this.transactions = ImmutableList.copyOf(transactions);
  }

  Transactions thatAre(Predicate<Transaction> condition) {
    return new Transactions(stream().filter(condition).toList());
  }

  Amount balance() {
    return balance(Amount.ZERO, (balance, tx) -> {});
  }

  Amount balance(Amount seed, BiConsumer<Amount, Transaction> consumer) {
    return stream().foldLeft(seed, (balance, transaction) -> {
      var newBalance = transaction.apply(balance);
      consumer.accept(newBalance, transaction);
      return newBalance;
    });
  }

  Transactions with(Transaction tx) {
    return new Transactions(ImmutableList.<Transaction>builder()
        .addAll(transactions)
        .add(tx)
        .build());
  }


  private StreamEx<Transaction> stream() {
    return StreamEx.of(transactions);
  }

  static Transactions none() {
    return new Transactions(ImmutableList.of());
  }

  static class Transaction {

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

    Transaction(Type type, Amount amount, LocalDateTime bookingTime) {
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
      return isWithdrawal() ? amount : ZERO;
    }

    Amount deposited() {
      return isDeposit() ? amount : ZERO;
    }

    boolean isDeposit() {
      return type == DEPOSIT;
    }

    boolean isWithdrawal() {
      return type == WITHDRAWAL;
    }

    boolean bookedOn(LocalDate date) {
      return bookingDate.isEqual(date);
    }

    boolean bookedBefore(LocalDate date) {
      return LocalDateRange.ofUnboundedStart(date).contains(bookingDate);
    }

    boolean bookedDuring(LocalDate from, LocalDate to) {
      return LocalDateRange.ofClosed(from, to).contains(bookingDate);
    }

    static Transaction withdrawalOf(Amount amount) {
      return new Transaction(WITHDRAWAL, amount, LocalDateTime.now(clock()));
    }

    static Transaction depositOf(Amount amount) {
      return new Transaction(DEPOSIT, amount, LocalDateTime.now(clock()));
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

    private Function<BankAccountTxRecord, Transaction> fromJooq() {
      return jooq -> new Transaction(
          Transaction.Type.valueOf(jooq.getType()),
          Amount.of(jooq.getAmount()),
          jooq.getBookingTime()
      );
    }

  }
}
