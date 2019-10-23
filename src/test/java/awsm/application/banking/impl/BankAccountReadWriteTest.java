package awsm.application.banking.impl;

import static awsm.application.banking.impl.BankAccount.Type.SAVINGS;
import static awsm.application.trading.impl.$.$;
import static org.assertj.core.api.Assertions.assertThat;

import org.jooq.DSLContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

@SpringBootTest
@Rollback
@DisplayName("bank account repository")
class BankAccountReadWriteTest {

  @Autowired
  PlatformTransactionManager txManager;

  @Autowired
  DSLContext dsl;

  @Test
  void supports_adding() {
    var tx = new TransactionTemplate(txManager);
    var limit = new WithdrawalLimit($("100.00"));
    var account = new BankAccount(SAVINGS, Iban.newlyGenerated(), limit);

    account.deposit($("50.00"));
    account.withdraw($("20.00"));

    tx.executeWithoutResult(whateverStatus -> account.saveNew(dsl));

    var id = account.id().orElseThrow();

    tx.executeWithoutResult(whateverStatus -> {
      var it = new BankAccount(dsl, id);
      assertThat(it.balance()).isEqualTo($("30.00"));
      it.deposit($("70.00"));
      it.save(dsl);
    });

    tx.executeWithoutResult(whateverStatus -> {
      var it = new BankAccount(dsl, id);
      assertThat(it.balance()).isEqualTo($("100.00"));
    });

  }

}