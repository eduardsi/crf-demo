package awsm.domain.banking;

import static awsm.domain.offers.DecimalNumber.ZERO;
import static awsm.infra.time.TimeMachine.clock;

import awsm.domain.offers.DecimalNumber;
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
      DecimalNumber apply(DecimalNumber amount, DecimalNumber balance) {
        return balance.plus(amount);
      }
    },

    WITHDRAWAL {
      @Override
      DecimalNumber apply(DecimalNumber amount, DecimalNumber balance) {
        return balance.minus(amount);
      }
    };

    abstract DecimalNumber apply(DecimalNumber amount, DecimalNumber balance);

    @Override
    public String toString() {
      return name().toLowerCase();
    }
  }

  private DecimalNumber amount;

  private LocalDateTime bookingTime = LocalDateTime.now(clock());

  @Enumerated(EnumType.STRING)
  private Type type;

  Transaction(Type type, DecimalNumber amount) {
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

  DecimalNumber apply(DecimalNumber balance) {
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

    DecimalNumber withdrawal() {
      return type == Type.WITHDRAWAL ? amount : ZERO;
    }

    DecimalNumber deposit() {
      return type == Type.DEPOSIT ? amount : ZERO;
    }
  }


}