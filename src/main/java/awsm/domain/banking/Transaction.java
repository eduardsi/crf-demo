package awsm.domain.banking;

import static awsm.domain.offers.DecimalNumber.ZERO;
import static awsm.infra.time.TimeMachine.clock;
import static java.time.format.DateTimeFormatter.ISO_DATE_TIME;
import static java.time.temporal.ChronoUnit.MINUTES;

import awsm.domain.offers.DecimalNumber;
import awsm.infra.hibernate.HibernateConstructor;
import awsm.infra.media.Media;
import java.time.LocalDate;
import java.time.LocalDateTime;
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

      @Override
      public String toString() {
        return "deposit";
      }
    },

    WITHDRAW {
      @Override
      DecimalNumber apply(DecimalNumber amount, DecimalNumber balance) {
        return balance.minus(amount);
      }

      @Override
      public String toString() {
        return "withdrawal";
      }
    };

    abstract DecimalNumber apply(DecimalNumber amount, DecimalNumber balance);

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

  void printTo(Media nested) {
    nested.print("time", bookingTime.truncatedTo(MINUTES).format(ISO_DATE_TIME));
    nested.print(type.toString(), amount.toString());
  }

  LocalDateTime bookingTime() {
    return bookingTime;
  }

  Amount amount() {
    return new Amount();
  }

  boolean isWithdrawal() {
    return type == Type.WITHDRAW;
  }

  boolean isDeposit() {
    return type == Type.DEPOSIT;
  }

  boolean isBooked(LocalDate someDay) {
    return bookingTime.toLocalDate().isEqual(someDay);
  }

  boolean isBookedWithin(LocalDateRange dateRange) {
    var bookingDate = bookingTime.toLocalDate();
    return dateRange.contains(bookingDate);
  }

  DecimalNumber apply(DecimalNumber balance) {
    return type.apply(amount, balance);
  }

  class Amount {

    private Amount() {
    }

    DecimalNumber withdrawal() {
      return isWithdrawal() ? amount : ZERO;
    }

    DecimalNumber deposit() {
      return isDeposit() ? amount : ZERO;
    }
  }


}