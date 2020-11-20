package dagger_games;

import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.impl.TimeBasedGenerator;
import jooq.tables.records.BankAccountTxRecord;
import org.jooq.DSLContext;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;

import static com.fasterxml.uuid.UUIDComparator.staticCompare;

public class Transaction implements Comparable<Transaction> {

    private final static TimeBasedGenerator ORDERED_UUID = Generators.timeBasedGenerator();

    private final BankAccountTxRecord self;

    Transaction(BankAccountTxRecord self) {
        this.self = self;
    }

    Transaction(String iban, TransactionType type, Amount amount, LocalDateTime bookingTime) {
        this.self = new BankAccountTxRecord()
                .setBankAccountIban(iban)
                .setUid(ORDERED_UUID.generate())
                .setAmount(amount)
                .setBookingTime(bookingTime)
                .setType(type);
    }

    LocalDateTime bookingTime() {
      return self.getBookingTime();
    }

    private LocalDate bookingDate() {
      return bookingTime().toLocalDate();
    }

    Amount apply(Amount balance) {
      return self.getType().apply(self.getAmount(), balance);
    }

    Amount withdrawn() {
      return isWithdrawal() ? self.getAmount() : Amount.ZERO;
    }

    public boolean isWithdrawal() {
        return self.getType().equals(TransactionType.WITHDRAWAL);
    }

    Amount deposited() {
      return self.getType().equals(TransactionType.DEPOSIT) ? self.getAmount() : Amount.ZERO;
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
        return staticCompare(this.self.getUid(), that.self.getUid());
    }
}