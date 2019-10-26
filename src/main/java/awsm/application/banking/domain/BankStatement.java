package awsm.application.banking.domain;

import static java.time.format.DateTimeFormatter.ISO_DATE;
import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME;
import static java.time.temporal.ChronoUnit.MINUTES;
import static javax.json.Json.createArrayBuilder;
import static javax.json.Json.createObjectBuilder;

import awsm.application.banking.domain.Transactions.Transaction;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import javax.money.MonetaryAmount;

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

  private void newEntry(Transaction tx, MonetaryAmount balance) {
    entries.add(new Entry(
        tx.bookingTime(),
        tx.withdrawn(),
        tx.deposited(),
        balance));
  }

  public String json() {
    var root = createObjectBuilder();

    root.add("startingBalance", createObjectBuilder()
        .add("amount", startingBalance.amount.getNumber() + "")
        .add("date", startingBalance.date.format(ISO_DATE)));

    root.add("closingBalance", createObjectBuilder()
        .add("amount", closingBalance.amount.getNumber() + "")
        .add("date", closingBalance.date.format(ISO_DATE)));

    var items = createArrayBuilder();
    entries.forEach(it -> items.add(createObjectBuilder()
        .add("time", it.time.format(ISO_LOCAL_DATE_TIME))
        .add("withdrawal", it.withdrawal.getNumber() + "")
        .add("deposit", it.deposit.getNumber() + "")
        .add("balance", it.balance.getNumber() + "")));

    root.add("transactions", items);

    return root.build().toString();
  }

  private static class Entry {

    private final LocalDateTime time;

    private final MonetaryAmount withdrawal;

    private final MonetaryAmount deposit;

    private final MonetaryAmount balance;

    private Entry(LocalDateTime time, MonetaryAmount withdrawal, MonetaryAmount deposit, MonetaryAmount balance) {
      this.time = time.truncatedTo(MINUTES);
      this.withdrawal = withdrawal;
      this.deposit = deposit;
      this.balance = balance;
    }

  }

  private static class Balance {

    private final MonetaryAmount amount;
    private final LocalDate date;

    private Balance(LocalDate date, MonetaryAmount amount) {
      this.amount = amount;
      this.date = date;
    }

  }
}
