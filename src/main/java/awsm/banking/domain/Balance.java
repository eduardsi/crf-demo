package awsm.banking.domain;

import awsm.banking.domain.core.Amount;
import one.util.streamex.StreamEx;

import java.util.stream.Stream;

public class Balance {

    private final Amount balance;

    Balance(Stream<Transaction> transactions) {
        this.balance = StreamEx.of(transactions).foldRight(Amount.ZERO, Transaction::apply);
    }

    boolean isPositive() {
        return balance.isGreaterThanOrEqualTo(Amount.ZERO);
    }

    Amount abs() {
        return balance.abs();
    }

}
