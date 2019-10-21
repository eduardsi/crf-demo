package awsm.application.banking.impl;

import static jooq.tables.BankAccount.BANK_ACCOUNT;
import static jooq.tables.BankAccountTx.BANK_ACCOUNT_TX;

import javax.sql.DataSource;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
class BankAccounts {

  private final JdbcTemplate jdbc;
  private final DataSource dataSource;

  BankAccounts(JdbcTemplate jdbc, DataSource dataSource) {
    this.jdbc = jdbc;
    this.dataSource = dataSource;
  }

  public long add(BankAccount account) {
    var bankAccountId = DSL.using(dataSource, SQLDialect.POSTGRES)
        .insertInto(BANK_ACCOUNT,
            BANK_ACCOUNT.IBAN,
            BANK_ACCOUNT.STATUS,
            BANK_ACCOUNT.TYPE,
            BANK_ACCOUNT.DAILY_LIMIT)
        .values(
            account.iban() + "",
            account.status().name(),
            account.type().name(),
            account.withdrawalLimit().dailyLimit().big())
        .returning(BANK_ACCOUNT.ID)
        .fetchOne()
        .getId();


    for (int i = 0; i < account.committedTransactions().size(); i++) {
      var tx = account.committedTransactions().get(i);
      DSL.using(dataSource, SQLDialect.POSTGRES)
          .insertInto(BANK_ACCOUNT_TX,
              BANK_ACCOUNT_TX.BANK_ACCOUNT_ID,
              BANK_ACCOUNT_TX.INDEX,
              BANK_ACCOUNT_TX.AMOUNT,
              BANK_ACCOUNT_TX.BOOKING_TIME,
              BANK_ACCOUNT_TX.TYPE)
          .values(bankAccountId, i, tx.amount().big(), tx.bookingTime(), tx.type().name())
          .execute();
    }

    return bankAccountId;
  }

  public BankAccount singleById(long id) {
    var transactions = jdbc.query("SELECT * FROM bank_account_tx tx WHERE tx.bank_account_id = ? order by index asc",
        (rs, rowNum) -> new Transaction(rs), id);

    var bankAccount = jdbc.queryForObject("SELECT b.* FROM bank_account b WHERE b.id = ?",
        (rs, rowNum) -> new BankAccount(rs, transactions), id);

    return bankAccount;
  }

}
