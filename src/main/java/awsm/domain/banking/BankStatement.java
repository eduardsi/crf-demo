package awsm.domain.banking;

import static java.time.format.DateTimeFormatter.ISO_DATE;
import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME;
import static java.time.temporal.ChronoUnit.MINUTES;
import static javax.json.Json.createArrayBuilder;
import static javax.json.Json.createObjectBuilder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;

class BankStatement {

  private final Collection<Entry> entries = new ArrayList<>();

  private final Balance closingBalance;

  private final Balance startingBalance;

  BankStatement(LocalDate from, LocalDate to, Transactions transactions) {
    var startingBalance = transactions.thatAre(tx -> tx.bookedBefore(from)).balance();
    var closingBalance = transactions
        .thatAre(tx -> tx.bookedDuring(from, to))
        .balance(startingBalance, (tx, balance) -> newEntry(tx, balance));

    this.startingBalance = new Balance(from, startingBalance);
    this.closingBalance = new Balance(to, closingBalance);
  }

  private void newEntry(Transactions.Transaction tx, Amount balance) {
    entries.add(new Entry(
        tx.bookingTime(),
        tx.withdrawn(),
        tx.deposited(),
        balance));
  }

  public String json() {
    var root = createObjectBuilder();

    root.add("startingBalance", createObjectBuilder()
        .add("amount", startingBalance.amount + "")
        .add("date", startingBalance.date.format(ISO_DATE)));

    root.add("closingBalance", createObjectBuilder()
        .add("amount", closingBalance.amount + "")
        .add("date", closingBalance.date.format(ISO_DATE)));

    var items = createArrayBuilder();
    entries.forEach(it -> items.add(createObjectBuilder()
        .add("awsm.time", it.time.format(ISO_LOCAL_DATE_TIME))
        .add("withdrawal", it.withdrawal + "")
        .add("deposit", it.deposit + "")
        .add("balance", it.balance + "")));

    root.add("transactions", items);

    return root.build().toString();
  }

  private static class Entry {

    private final LocalDateTime time;

    private final Amount withdrawal;

    private final Amount deposit;

    private final Amount balance;

    private Entry(LocalDateTime time, Amount withdrawal, Amount deposit, Amount balance) {
      this.time = time.truncatedTo(MINUTES);
      this.withdrawal = withdrawal;
      this.deposit = deposit;
      this.balance = balance;
    }

  }

  private static class Balance {

    private final Amount amount;
    private final LocalDate date;

    private Balance(LocalDate date, Amount amount) {
      this.amount = amount;
      this.date = date;
    }

  }
}
