package awsm.domain.banking;

import static awsm.domain.banking.Transaction.Type.DEPOSIT;
import static awsm.domain.banking.Transaction.Type.WITHDRAW;
import static com.google.common.collect.ImmutableList.toImmutableList;

import awsm.domain.offers.DecimalNumber;
import java.util.List;
import java.util.function.Predicate;
import one.util.streamex.StreamEx;
import org.threeten.extra.LocalDateRange;

class Transactions {

  private List<Transaction> transactions;

  Transactions(List<Transaction> transactions) {
    this.transactions = transactions;
  }

  Transaction withdrawal(DecimalNumber amount) {
    var tx = new Transaction(WITHDRAW, amount);
    transactions.add(tx);
    return tx;
  }

  Transaction deposit(DecimalNumber amount) {
    var tx = new Transaction(DEPOSIT, amount);
    transactions.add(tx);
    return tx;
  }

  Transactions within(LocalDateRange dateRange) {
    return new Transactions(transactions.stream().filter(tx -> tx.isBookedWithin(dateRange)).collect(toImmutableList()));
  }

  StreamEx<Transaction> stream() {
    return StreamEx.of(transactions.stream());
  }

  DecimalNumber sum() {
    return sumIf(tx -> true);
  }

  DecimalNumber sumIf(Predicate<Transaction> condition) {
    return stream().filter(condition).foldRight(DecimalNumber.ZERO, Transaction::apply);
  }

}
