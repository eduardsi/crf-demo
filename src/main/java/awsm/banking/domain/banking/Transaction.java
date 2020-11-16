package awsm.banking.domain.banking;

import awsm.banking.domain.core.DomainEntity;
import awsm.banking.domain.core.Amount;
import org.threeten.extra.LocalDateRange;

import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.UUID;

import static awsm.infrastructure.clock.TimeMachine.clock;

@Embeddable
class Transaction implements DomainEntity<Transaction> {

    enum Type {
        DEPOSIT {
            @Override
            Amount apply(Amount amount, Amount balance) {
                return balance.add(amount);
            }
        },
        WITHDRAW {
            @Override
            Amount apply(Amount amount, Amount balance) {
                return balance.subtract(amount);
            }
        };

        abstract Amount apply(Amount amount, Amount balance);
    }

    private UUID uid;

    private Amount amount;

    private LocalDateTime bookingTime;

    @Enumerated(EnumType.STRING)
    private Type type;

    private Transaction(Type type, Amount amount, LocalDateTime bookingTime) {
        this.uid = UUID.randomUUID();
        this.type = type;
        this.amount = amount;
        this.bookingTime = bookingTime;
    }

    private Transaction() {
    }

    public UUID uid() {
        return uid;
    }

    Amount apply(Amount balance) {
        return type.apply(amount, balance);
    }

    Amount withdrawn() {
        return isWithdrawal() ? amount : Amount.ZERO;
    }

    boolean isWithdrawal() {
        return type == Type.WITHDRAW;
    }

    Amount deposited() {
        return isDeposit() ? amount : Amount.ZERO;
    }

    boolean isDeposit() {
        return type == Type.DEPOSIT;
    }

    LocalDateTime bookingTime() {
        return bookingTime;
    }

    boolean bookedIn(LocalDate date) {
        return bookingTime.toLocalDate().isEqual(date);
    }

    boolean bookedBefore(LocalDate dateExclusive) {
        return LocalDateRange.ofUnboundedStart(dateExclusive).contains(bookingTime.toLocalDate());
    }

    boolean bookedDuring(LocalDate fromInclusive, LocalDate toInclusive) {
        return LocalDateRange.ofClosed(fromInclusive, toInclusive).contains(bookingTime.toLocalDate());
    }

    boolean bookedIn(Month month) {
        return bookingTime.toLocalDate().getMonth().equals(month);
    }

    static Transaction withdrawalOf(Amount amount) {
        return new Transaction(Type.WITHDRAW, amount, LocalDateTime.now(clock()));
    }

    static Transaction depositOf(Amount amount) {
        return new Transaction(Type.DEPOSIT, amount, LocalDateTime.now(clock()));
    }

}
