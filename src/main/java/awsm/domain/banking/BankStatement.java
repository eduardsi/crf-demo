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

  private final Collection<TxEntry> entries = new ArrayList<>();

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

  private void enter(Transaction tx, DecimalNumber runningBalance) {
    entries.add(new TxEntry(
        tx.bookingTime(),
        tx.amount().withdrawal(),
        tx.amount().deposit(),
        runningBalance));
  }

  public void printTo(Media media) {
    media.print("startingBalance", nested -> startingBalance.printTo(nested));
    media.print("closingBalance", nested -> closingBalance.printTo(nested));
    media.print("transactions", entries, (nested, entry) -> entry.printTo(nested));
  }

  private static class TxEntry {

    private final LocalDateTime time;

    private final DecimalNumber withdrawal;

    private final DecimalNumber deposit;

    private final DecimalNumber balance;

    private TxEntry(LocalDateTime time, DecimalNumber withdrawal, DecimalNumber deposit, DecimalNumber balance) {
      this.time = time.truncatedTo(MINUTES);
      this.withdrawal = withdrawal;
      this.deposit = deposit;
      this.balance = balance;
    }

    private void printTo(Media media) {
      media.print("time", time.format(ISO_LOCAL_DATE_TIME));
      media.print("withdrawal", withdrawal.toString());
      media.print("deposit", deposit.toString());
      media.print("balance", balance.toString());
    }
  }

  private static class Balance {

    private final DecimalNumber amount;
    private final LocalDate date;

    private Balance(LocalDate date, DecimalNumber amount) {
      this.amount = amount;
      this.date = date;
    }

    private void printTo(Media media) {
      media.print("amount", amount.toString());
      media.print("date", date.format(ISO_DATE));
    }


  }
}
