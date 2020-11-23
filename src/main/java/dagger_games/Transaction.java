package dagger_games;

import de.huxhorn.sulky.ulid.ULID;
import jooq.tables.records.BankAccountTxRecord;
import org.jooq.DSLContext;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Transaction implements Comparable<Transaction> {

    private static final ULID ulid = new ULID();

    private final BankAccountTxRecord self;

    Transaction(BankAccountTxRecord self) {
        this.self = self;
    }

    Transaction(String iban, TransactionType type, Amount amount, LocalDateTime bookingTime) {
        this.self = new BankAccountTxRecord()
                .setBankAccountIban(iban)
                .setUid(ulid.nextULID())
                .setAmount(amount)
                .setBookingTime(bookingTime)
                .setType(type);
    }

    LocalDateTime bookingTime() {
      return self.bookingTime();
    }

    private LocalDate bookingDate() {
      return bookingTime().toLocalDate();
    }

    Amount apply(Amount balance) {
      return self.type().apply(self.amount(), balance);
    }

    Amount withdrawn() {
      return isWithdrawal() ? self.amount() : Amount.ZERO;
    }

    public boolean isWithdrawal() {
        return self.type().equals(TransactionType.WITHDRAWAL);
    }

    Amount deposited() {
      return self.type().equals(TransactionType.DEPOSIT) ? self.amount() : Amount.ZERO;
    }

    boolean bookedOn(LocalDate date) {
      return bookingDate().isEqual(date);
    }

    void save(DSLContext dsl) {
        self.attach(dsl.configuration());
        self.store();
    }

    @Override
    public int compareTo(Transaction that) {
        return this.self.uid().compareTo(that.self.uid());
    }
}