package awsm.domain.banking;

import static com.google.common.collect.ImmutableList.copyOf;

import awsm.domain.offers.DecimalNumber;
import java.util.Collection;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import one.util.streamex.StreamEx;

class Transactions {

  private Collection<Transaction> transactions;

  Transactions(List<Transaction> transactions) {
    this.transactions = copyOf(transactions);
  }

  Transactions thatAre(Predicate<Transaction> condition) {
    return new Transactions(stream().filter(condition).toList());
  }

  DecimalNumber balance(DecimalNumber startingBalance, BiConsumer<DecimalNumber, Transaction> balanceConsumer) {
    return stream().foldLeft(startingBalance, (runningBalance, tx) -> {
      var newBalance = tx.apply(runningBalance);
      balanceConsumer.accept(newBalance, tx);
      return newBalance;
    });
  }

  DecimalNumber balance() {
    return balance(DecimalNumber.ZERO, (decimalNumber, tx) -> {});
  }

  private StreamEx<Transaction> stream() {
    return StreamEx.of(transactions);
  }

}
