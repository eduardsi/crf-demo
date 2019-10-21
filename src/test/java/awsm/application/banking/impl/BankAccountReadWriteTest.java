package awsm.application.banking.impl;

import static awsm.application.banking.impl.BankAccount.Type.SAVINGS;
import static awsm.application.trading.impl.$.$;
import static org.assertj.core.api.Assertions.assertThat;

import awsm.util.tx.Transactions;
import javax.sql.DataSource;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.PlatformTransactionManager;

@SpringBootTest
@Rollback
@DisplayName("bank account")
class BankAccountReadWriteTest {

  @Autowired
  DataSource dataSource;

  @Autowired
  PlatformTransactionManager txManager;

  @Test
  void supports_adding() {
    var transactions = new Transactions(txManager);
    var limit = new WithdrawalLimit($("100.00"));
    var account = new BankAccount(SAVINGS, limit);

    account.deposit($("50.00"));
    account.withdraw($("20.00"));
    var id = transactions.wrap(() -> account.saveNew(dataSource)).get();

    transactions.wrap(() -> {
      var it = new BankAccount(dataSource, id);
      assertThat(it.balance()).isEqualTo($("30.00"));
      it.deposit($("70.00"));
      it.save(dataSource);
    }).run();

    transactions.wrap(() -> {
      var it = new BankAccount(dataSource, id);
      assertThat(it.balance()).isEqualTo($("100.00"));
    }).run();

  }

}