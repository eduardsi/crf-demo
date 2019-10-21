package awsm.application.banking.impl;

import static jooq.tables.BankAccountTx.BANK_ACCOUNT_TX;

import awsm.application.trading.impl.$;
import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.sql.DataSource;
import one.util.streamex.StreamEx;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

class Transactions {

  private final ImmutableList<Transaction> transactions;

  private Transactions(List<Transaction> transactions) {
    this.transactions = ImmutableList.copyOf(transactions);
  }

  Transactions(DataSource dataSource, long id) {
    this(DSL.using(dataSource, SQLDialect.POSTGRES)
        .selectFrom(BANK_ACCOUNT_TX)
        .where(BANK_ACCOUNT_TX.BANK_ACCOUNT_ID.equal(id))
        .orderBy(BANK_ACCOUNT_TX.INDEX.asc())
        .fetchStream()
        .map(Transaction::new)
        .collect(Collectors.toList()));

  }

  Transactions thatAre(Predicate<Transaction> condition) {
    return new Transactions(stream().filter(condition).toList());
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

  void save(DataSource dataSource, long bankAccountId) {
    var dsl = DSL.using(dataSource, SQLDialect.POSTGRES);
    dsl
        .deleteFrom(BANK_ACCOUNT_TX)
        .where(BANK_ACCOUNT_TX.BANK_ACCOUNT_ID.eq(bankAccountId))
        .execute();

    for (int i = 0; i < transactions.size(); i++) {
      var tx = transactions.get(i);
      dsl
          .insertInto(BANK_ACCOUNT_TX,
              BANK_ACCOUNT_TX.BANK_ACCOUNT_ID,
              BANK_ACCOUNT_TX.INDEX,
              BANK_ACCOUNT_TX.AMOUNT,
              BANK_ACCOUNT_TX.BOOKING_TIME,
              BANK_ACCOUNT_TX.TYPE)
          .values(bankAccountId, i, tx.amount().big(), tx.bookingTime(), tx.type().name())
          .execute();
    }
  }

  static Transactions none() {
    return new Transactions(ImmutableList.of());
  }

}
