package awsm.application.banking.impl;

import static java.time.format.DateTimeFormatter.ISO_DATE;
import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME;
import static java.time.temporal.ChronoUnit.MINUTES;
import static javax.json.Json.createArrayBuilder;
import static javax.json.Json.createObjectBuilder;

import awsm.infrastructure.modeling.Amount;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;

class BankStatement {

  private final Collection<Entry> transactions = new ArrayList<>();

  private final Balance closingBalance;

  private final Balance startingBalance;

  BankStatement(LocalDate from, LocalDate to, Transactions transactions) {
    var startingBalance = transactions.thatAre(Transactions.Tx.bookedBefore(from)).balance();
    var closingBalance = transactions
        .thatAre(Transactions.Tx.bookedDuring(from, to))
        .balance(startingBalance, (balance, tx) -> enter(tx, balance));

    this.startingBalance = new Balance(from, startingBalance);
    this.closingBalance = new Balance(to, closingBalance);
  }

  private void enter(Transactions.Tx tx, Amount balance) {
    transactions.add(new Entry(
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
    transactions.forEach(tx -> items.add(createObjectBuilder()
        .add("time", tx.time.format(ISO_LOCAL_DATE_TIME))
        .add("withdrawal", tx.withdrawal + "")
        .add("deposit", tx.deposit + "")
        .add("balance", tx.balance + "")));

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
