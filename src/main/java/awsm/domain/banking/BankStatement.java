package awsm.domain.banking;

import static awsm.domain.banking.Transaction.bookedBefore;
import static awsm.domain.banking.Transaction.bookedDuring;
import static java.time.format.DateTimeFormatter.ISO_DATE;
import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME;
import static java.time.temporal.ChronoUnit.MINUTES;

import awsm.domain.offers.DecimalNumber;
import awsm.infra.media.Media;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;

class BankStatement {

  private final Collection<Entry> entries = new ArrayList<>();

  private final Balance closingBalance;

  private final Balance startingBalance;

  BankStatement(LocalDate from, LocalDate to, Transactions transactions) {
    var startingBalance = transactions.thatAre(bookedBefore(from)).balance();
    var closingBalance = transactions
        .thatAre(bookedDuring(from, to))
        .balance(startingBalance, (balance, tx) -> enter(tx, balance));

    this.startingBalance = new Balance(from, startingBalance);
    this.closingBalance = new Balance(to, closingBalance);
  }

  private void enter(Transaction tx, DecimalNumber balance) {
    entries.add(new Entry(
        tx.bookingTime(),
        tx.amount().withdrawal(),
        tx.amount().deposit(),
        balance));
  }

  // alternative: public DTO (if multiple formats),
  // but package private constructor that accepts entity.
  public void printTo(Media media) {
    media.print("startingBalance", nested -> {
      nested.print("amount", startingBalance.amount);
      nested.print("date", startingBalance.date.format(ISO_DATE));
    });
    media.print("closingBalance", nested -> {
      nested.print("amount", closingBalance.amount);
      nested.print("date", closingBalance.date.format(ISO_DATE));
    });
    media.print("transactions", entries, (nested, entry) -> {
      nested.print("time", entry.time.format(ISO_LOCAL_DATE_TIME));
      nested.print("withdrawal", entry.withdrawal);
      nested.print("deposit", entry.deposit);
      nested.print("balance", entry.balance);
    });
  }

  private static class Entry {

    private final LocalDateTime time;

    private final DecimalNumber withdrawal;

    private final DecimalNumber deposit;

    private final DecimalNumber balance;

    private Entry(LocalDateTime time, DecimalNumber withdrawal, DecimalNumber deposit, DecimalNumber balance) {
      this.time = time.truncatedTo(MINUTES);
      this.withdrawal = withdrawal;
      this.deposit = deposit;
      this.balance = balance;
    }

  }

  private static class Balance {

    private final DecimalNumber amount;
    private final LocalDate date;

    private Balance(LocalDate date, DecimalNumber amount) {
      this.amount = amount;
      this.date = date;
    }

  }
}
