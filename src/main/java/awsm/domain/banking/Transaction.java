package awsm.domain.banking;

import static awsm.domain.offers.$.ZERO;
import static awsm.infra.time.TimeMachine.clock;

import awsm.domain.DomainEntity;
import awsm.domain.offers.$;
import awsm.infra.hibernate.HibernateConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.function.Predicate;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import org.threeten.extra.LocalDateRange;

@Embeddable
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

  private $ amount;

  private LocalDateTime bookingTime = LocalDateTime.now(clock());

  @Enumerated(EnumType.STRING)
  private Type type;

  private Transaction(Type type, $ amount) {
    this.type = type;
    this.amount = amount;
  }

  @HibernateConstructor
  private Transaction() {
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