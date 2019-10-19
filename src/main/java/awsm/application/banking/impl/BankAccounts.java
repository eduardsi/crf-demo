package awsm.application.banking.impl;

import java.util.HashMap;
import javax.sql.DataSource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
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
    var jdbcInsert = new SimpleJdbcInsert(dataSource)
        .withTableName("bank_account")
        .usingGeneratedKeyColumns("id");

    var args = new HashMap<String, Object>();
    args.put("iban", account.iban() + "");
    args.put("status", account.status().name());
    args.put("type", account.type().name());
    args.put("daily_limit", account.withdrawalLimit().dailyLimit().big());

    var bankAccountId = (long) jdbcInsert.executeAndReturnKey(args);

    for (int i = 0; i < account.committedTransactions().size(); i++) {
      var tx = account.committedTransactions().get(i);
      jdbc.update(
          """
              INSERT INTO bank_account_tx
              (bank_account_id, index, amount, booking_time, type) VALUES
              (?, ?, ?, ?, ?)
          """,
          bankAccountId, i, tx.amount().big(), tx.bookingTime(), tx.type().name());
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
