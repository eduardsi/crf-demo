package awsm.application.banking.impl;

import static awsm.application.banking.impl.BankAccount.Type.SAVINGS;
import static awsm.infrastructure.modeling.Amount.of;
import static org.assertj.core.api.Assertions.assertThat;

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
  BankAccount.Repository repository;

  @Test
  void supports_adding() {
    var tx = new TransactionTemplate(txManager);
    var limit = new WithdrawalLimit(of("100.00"));
    var account = new BankAccount(SAVINGS, Iban.newlyGenerated(), limit);

    account.deposit(of("50.00"));
    account.withdraw(of("20.00"));

    tx.executeWithoutResult(whateverStatus -> account.saveNew(repository));

    var id = account.id().orElseThrow();

    tx.executeWithoutResult(whateverStatus -> {
      var it = repository.singleBy(id);
      assertThat(it.balance()).isEqualTo(of("30.00"));
      it.deposit(of("70.00"));
      it.save(repository);
    });

    tx.executeWithoutResult(whateverStatus -> {
      var it = repository.singleBy(id);
      assertThat(it.balance()).isEqualTo(of("100.00"));
    });

  }

}