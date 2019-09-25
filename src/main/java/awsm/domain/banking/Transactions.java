package awsm.domain.banking;

import static com.google.common.collect.ImmutableList.copyOf;

import awsm.domain.offers.$;
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
    return new Transactions(stream().filter(condition).toImmutableList());
  }

  $ balance() {
    return balance($.ZERO, (balance, tx) -> {});
  }

  $ balance($ seed, BiConsumer<$, Transaction> consumer) {
    return stream().foldLeft(seed, (balance, transaction) -> {
      var newBalance = transaction.apply(balance);
      consumer.accept(newBalance, transaction);
      return newBalance;
    });
  }

  private StreamEx<Transaction> stream() {
    return StreamEx.of(transactions);
  }

}
