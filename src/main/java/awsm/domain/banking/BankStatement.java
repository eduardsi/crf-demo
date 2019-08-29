package awsm.domain.banking;

import static java.time.format.DateTimeFormatter.ISO_DATE;
import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME;
import static java.time.temporal.ChronoUnit.MINUTES;
import static javax.json.Json.createArrayBuilder;
import static javax.json.Json.createObjectBuilder;

import awsm.domain.offers.DecimalNumber;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import javax.json.JsonObject;
import org.threeten.extra.LocalDateRange;

class BankStatement {

  private final Collection<TxEntry> entries = new ArrayList<>();

  private final Balance closingBalance;

  private final Balance startingBalance;

  BankStatement(LocalDate from, LocalDate to, Transactions transactions) {
    var startingBalance = transactions.within(LocalDateRange.ofUnboundedStart(from)).sum();

    var closingBalance = transactions
        .within(LocalDateRange.ofClosed(from, to))
        .stream()
        .foldLeft(startingBalance, (balance, tx) -> {
          var runningBalance = tx.apply(balance);
          entries.add(new TxEntry(
              tx.bookingTime(),
              tx.amount().withdrawal(),
              tx.amount().deposit(),
              runningBalance));
          return runningBalance;
        });

    this.startingBalance = new Balance(from, startingBalance);
    this.closingBalance = new Balance(to, closingBalance);
  }

  public String json() {
    var self = createObjectBuilder();
    self.add("startingBalance", startingBalance.json());
    self.add("closingBalance", closingBalance.json());

    var children = createArrayBuilder();
    entries.forEach(txEntry -> children.add(txEntry.json()));
    self.add("transactions", children);

    return self.build().toString();
  }

  static class TxEntry {

    private final LocalDateTime time;

    private final DecimalNumber withdrawal;

    private final DecimalNumber deposit;

    private final DecimalNumber balance;

    TxEntry(LocalDateTime time, DecimalNumber withdrawal, DecimalNumber deposit, DecimalNumber balance) {
      this.time = time.truncatedTo(MINUTES);
      this.withdrawal = withdrawal;
      this.deposit = deposit;
      this.balance = balance;
    }

    private JsonObject json() {
      var self = createObjectBuilder();
      self.add("time", time.format(ISO_LOCAL_DATE_TIME));
      self.add("withdrawal", withdrawal.toString());
      self.add("deposit", deposit.toString());
      self.add("balance", balance.toString());
      return self.build();
    }
  }

  static class Balance {

    private final DecimalNumber amount;
    private final LocalDate date;

    Balance(LocalDate date, DecimalNumber amount) {
      this.amount = amount;
      this.date = date;
    }

    private JsonObject json() {
      var self = createObjectBuilder();
      self.add("amount", amount.toString());
      self.add("date", date.format(ISO_DATE));
      return self.build();
    }
  }
}
