package awsm.application.banking.impl;

import static awsm.application.banking.impl.BankAccount.Type.SAVINGS;
import static awsm.application.trading.impl.$.$;
import static org.assertj.core.api.Assertions.assertThat;

import awsm.util.tx.Transactions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.PlatformTransactionManager;

@SpringBootTest
@Rollback
@DisplayName("bank account repository")
class BankAccountsTest {

  @Autowired
  BankAccounts accounts;

  @Autowired
  PlatformTransactionManager txManager;

  @Test
  void supports_adding() {
    var transactions = new Transactions(txManager);
    var limit = new WithdrawalLimit($("100.00"));
    var newAccount = new BankAccount(SAVINGS, limit);

    newAccount.deposit($("50.00"));
    newAccount.withdraw($("20.00"));
    var newAccountId = transactions.wrap(() -> accounts.add(newAccount)).get();

    transactions.wrap(() -> {
      var existingAccount = accounts.singleById(newAccountId);
      assertThat(existingAccount.balance()).isEqualTo($("30.00"));
    }).run();
  }

}