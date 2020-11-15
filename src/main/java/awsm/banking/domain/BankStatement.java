package awsm.banking.domain;

import awsm.banking.domain.core.Amount;
import one.util.streamex.StreamEx;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;

import static java.time.format.DateTimeFormatter.ISO_DATE;
import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME;
import static java.time.temporal.ChronoUnit.MINUTES;
import static javax.json.Json.createArrayBuilder;
import static javax.json.Json.createObjectBuilder;

class BankStatement {

  private final Collection<Entry> entries = new ArrayList<>();

  private final BalanceOnADate closingBalance;

  private final BalanceOnADate startingBalance;

  BankStatement(LocalDate fromInclusive, LocalDate toInclusive, Collection<Transaction> transactions) {
    var startingBalance =  StreamEx
            .of(transactions)
            .filter(tx -> tx.bookedBefore(fromInclusive))
            .foldRight(Amount.ZERO, Transaction::apply);

    var closingBalance = StreamEx
            .of(transactions)
            .filter(tx -> tx.bookedDuring(fromInclusive, toInclusive))
            .foldLeft(startingBalance, (amount, tx) -> {
              var balance = tx.apply(amount);
              newEntry(tx, balance);
              return balance;
    });

    this.startingBalance = new BalanceOnADate(fromInclusive, startingBalance);
    this.closingBalance = new BalanceOnADate(toInclusive, closingBalance);
  }

  private void newEntry(Transaction tx, Amount balance) {
    entries.add(new Entry(
        tx.bookingTime(),
        tx.withdrawn(),
        tx.deposited(),
        balance));
  }

  public String json() {
    var root = createObjectBuilder();

    root.add("startingBalance", createObjectBuilder()
        .add("amount", startingBalance.balance + "")
        .add("date", startingBalance.date.format(ISO_DATE)));

    root.add("closingBalance", createObjectBuilder()
        .add("amount", closingBalance.balance + "")
        .add("date", closingBalance.date.format(ISO_DATE)));

    var items = createArrayBuilder();
    entries.forEach(it -> items.add(createObjectBuilder()
        .add("time", it.time.format(ISO_LOCAL_DATE_TIME))
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

  private static class BalanceOnADate {

    private final Amount balance;
    private final LocalDate date;

    private BalanceOnADate(LocalDate date, Amount balance) {
      this.balance = balance;
      this.date = date;
    }

  }
}