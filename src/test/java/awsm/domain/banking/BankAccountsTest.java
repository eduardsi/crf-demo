package awsm.domain.banking;

import static awsm.domain.offers.$.$;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD;

import awsm.util.tx.Transactions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.PlatformTransactionManager;

@SpringBootTest
@DirtiesContext(classMode = BEFORE_EACH_TEST_METHOD)
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
    var newAccount = new BankAccount(limit);

    newAccount.deposit($("50.00"));
    newAccount.withdraw($("20.00"));
    transactions.wrap(() -> accounts.add(newAccount)).run();

    assertThat(newAccount.id()).isNotNull();

    transactions.wrap(() -> {
      var existingAccount = accounts.singleById(newAccount.id());
      assertThat(existingAccount.balance()).isEqualTo($("30.00"));
    }).run();
  }

}