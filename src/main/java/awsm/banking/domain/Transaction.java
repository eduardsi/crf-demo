package awsm.banking.domain;

import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;

import static awsm.infrastructure.clock.TimeMachine.clock;

@Embeddable
class Transaction {

    enum Type {
        DEPOSIT {
            @Override
            BigDecimal apply(BigDecimal amount, BigDecimal balance) {
                return balance.add(amount);
            }
        },
        WITHDRAW {
            @Override
            BigDecimal apply(BigDecimal amount, BigDecimal balance) {
                return balance.subtract(amount);
            }
        };

        abstract BigDecimal apply(BigDecimal amount, BigDecimal balance);
    }

    private BigDecimal amount;

    private LocalDateTime bookingTime;


    @Enumerated(EnumType.STRING)
    private Type type;

    private Transaction(Type type, BigDecimal amount, LocalDateTime bookingTime) {
        this.type = type;
        this.amount = amount;
        this.bookingTime = bookingTime;
    }

    private Transaction() {
    }

    BigDecimal apply(BigDecimal balance) {
        return type.apply(amount, balance);
    }

    BigDecimal withdrawn() {
        return isWithdrawal() ? amount : BigDecimal.ZERO;
    }

    boolean isWithdrawal() {
        return type == Type.WITHDRAW;
    }

    BigDecimal deposited() {
        return isDeposit() ? amount : BigDecimal.ZERO;
    }

    boolean isDeposit() {
        return type == Type.DEPOSIT;
    }

    boolean bookedIn(LocalDate date) {
        return bookingTime.toLocalDate().isEqual(date);
    }

    boolean bookedIn(Month month) {
        return bookingTime.toLocalDate().getMonth().equals(month);
    }

    static Transaction withdrawalOf(BigDecimal amount) {
        return new Transaction(Type.WITHDRAW, amount, LocalDateTime.now(clock()));
    }

    static Transaction depositOf(BigDecimal amount) {
        return new Transaction(Type.DEPOSIT, amount, LocalDateTime.now(clock()));
    }

}
