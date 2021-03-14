package awsm.domain.banking.aml;

import awsm.domain.banking.BankAccountRepository;
import awsm.domain.banking.WithdrawalHappened;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
class MarkTransactionForManualReviewOnWithdrawal {

  private final Logger logger =
      LoggerFactory.getLogger(MarkTransactionForManualReviewOnWithdrawal.class);

  private final BankAccountRepository accountRepository;

  MarkTransactionForManualReviewOnWithdrawal(BankAccountRepository accountRepository) {
    this.accountRepository = accountRepository;
  }

  @TransactionalEventListener
  public void trigger(WithdrawalHappened event) {
    var account = accountRepository.getOne(event.iban());
    var txUid = event.txUid();
    var tx = account.tx(txUid);

    if (tx.satisfies(new IsManualReviewNeeded())) {
      logger.info("Tx {} has been marked for manual review", txUid);
    }
  }
}
