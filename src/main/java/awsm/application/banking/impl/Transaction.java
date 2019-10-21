package awsm.application.banking.impl;

import static awsm.application.trading.impl.$.$;
import static awsm.application.trading.impl.$.ZERO;
import static awsm.infrastructure.time.TimeMachine.clock;

import awsm.application.trading.impl.$;
import awsm.infrastructure.modeling.DomainEntity;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.function.Predicate;
import jooq.tables.records.BankAccountTxRecord;
import org.threeten.extra.LocalDateRange;

class Transaction implements DomainEntity {

  enum Type {
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

  private Transaction(Type type, $ amount) {
    this.type = type;
    this.amount = amount;
    this.bookingTime = LocalDateTime.now(clock());
  }

  Transaction(ResultSet rs) throws SQLException {
    this.amount = $(rs.getBigDecimal("amount"));
    this.bookingTime = rs.getTimestamp("booking_time").toLocalDateTime();
    this.type = Type.valueOf(rs.getString("type"));
  }

  Transaction(BankAccountTxRecord rec) {
    this.amount = $(rec.getAmount());
    this.bookingTime = rec.getBookingTime();
    this.type = Type.valueOf(rec.getType());
  }

  LocalDateTime bookingTime() {
    return bookingTime;
  }

  $ amount() {
    return amount;
  }

  Type type() {
    return type;
  }

  private LocalDate bookingDate() {
    return bookingTime.toLocalDate();
  }

  $ apply($ balance) {
    return type.apply(amount, balance);
  }

  $ withdrawn() {
    return __(isWithdrawal()) ? amount : ZERO;
  }

  $ deposited() {
    return __(isDeposit()) ? amount : ZERO;
  }

  static Predicate<Transaction> isWithdrawal() {
    return tx -> tx.type == Type.WITHDRAWAL;
  }

  private static Predicate<Transaction> isDeposit() {
    return tx -> tx.type == Type.DEPOSIT;
  }

  static Predicate<Transaction> bookedOn(LocalDate date) {
    return tx -> tx.bookingDate().isEqual(date);
  }

  static Predicate<Transaction> bookedBefore(LocalDate date) {
    return tx -> LocalDateRange.ofUnboundedStart(date).contains(tx.bookingDate());
  }

  static Predicate<Transaction> bookedDuring(LocalDate from, LocalDate to) {
    return tx -> LocalDateRange.ofClosed(from, to).contains(tx.bookingDate());
  }

  static Transaction withdrawalOf($ amount) {
    return new Transaction(Type.WITHDRAWAL, amount);
  }

  static Transaction depositOf($ amount) {
    return new Transaction(Type.DEPOSIT, amount);
  }

}