package awsm.domain.banking;

import static org.assertj.core.api.Assertions.assertThat;

import awsm.domain.offers.$;
import awsm.util.tx.Transactions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.PlatformTransactionManager;

@SpringBootTest
class BankAccountsTest {

  @Autowired
  BankAccounts accounts;

  @Autowired
  PlatformTransactionManager txManager;

  @Test
  void supports_adding() {
    var transactions = new Transactions(txManager);
    var limit = new WithdrawalLimit($.of("100.00"));
    var account = new BankAccount(limit);

    account.deposit($.of("50.00"));
    account.withdraw($.of("20.00"));
    transactions.wrap(() -> accounts.add(account)).run();

    assertThat(account.id()).isNotNull();
  }

}