package awsm.application.banking.impl;

import awsm.application.trading.impl.$;
import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import one.util.streamex.StreamEx;

class Transactions {

  private final List<Transaction> transactions;

  private Transactions(List<Transaction> transactions) {
    this.transactions = transactions;
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

  Transactions with(Transaction tx) {
    return new Transactions(ImmutableList.<Transaction>builder()
        .addAll(transactions)
        .add(tx)
        .build());
  }

  private StreamEx<Transaction> stream() {
    return StreamEx.of(transactions);
  }

  static Transactions unmodifiable(List<Transaction> transactions) {
    return new Transactions(ImmutableList.copyOf(transactions));
  }
}
