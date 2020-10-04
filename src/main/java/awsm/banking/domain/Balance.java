package awsm.banking.domain;

import one.util.streamex.StreamEx;

import java.math.BigDecimal;
import java.util.stream.Stream;

public class Balance {

    private final BigDecimal balance;

    Balance(Stream<Transaction> transactions) {
        this.balance = StreamEx.of(transactions).foldRight(BigDecimal.ZERO, Transaction::apply);
    }

    boolean isPositive() {
        return balance.compareTo(BigDecimal.ZERO) >= 0;
    }

    BigDecimal abs() {
        return balance.abs();
    }
}
