package awsm.domain.banking;

import static awsm.domain.offers.$.ZERO;
import static awsm.infra.time.TimeMachine.clock;

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
class Transaction {

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

  Transaction(Type type, $ amount) {
    this.type = type;
    this.amount = amount;
  }

  @HibernateConstructor
  private Transaction() {
  }

  LocalDateTime bookingTime() {
    return bookingTime;
  }

  LocalDate bookingDate() {
    return bookingTime.toLocalDate();
  }

  Amount amount() {
    return new Amount();
  }

  Type type() {
    return type;
  }

  $ apply($ balance) {
    return type.apply(amount, balance);
  }

  static Predicate<Transaction> bookedBefore(LocalDate date) {
    return tx -> LocalDateRange.ofUnboundedStart(date).contains(tx.bookingDate());
  }

  static Predicate<Transaction> bookedDuring(LocalDate from, LocalDate to) {
    return tx -> LocalDateRange.ofClosed(from, to).contains(tx.bookingDate());
  }

  class Amount {

    private Amount() {
    }

    $ withdrawal() {
      return type == Type.WITHDRAWAL ? amount : ZERO;
    }

    $ deposit() {
      return type == Type.DEPOSIT ? amount : ZERO;
    }
  }


}