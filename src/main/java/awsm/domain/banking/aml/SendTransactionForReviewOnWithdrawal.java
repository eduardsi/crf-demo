package awsm.domain.banking.aml;

import awsm.domain.banking.BankAccountRepository;
import awsm.domain.banking.WithdrawalHappened;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
class SendTransactionForReviewOnWithdrawal {

  private final BankAccountRepository accounts;

  SendTransactionForReviewOnWithdrawal(BankAccountRepository accounts) {
    this.accounts = accounts;
  }

  @TransactionalEventListener
  public void trigger(WithdrawalHappened event) {
    var account = accounts.getOne(event.iban());
    var txUid = event.txUid();
    var tx = account.tx(txUid);

    if (tx.satisfies(new IsManualReviewNeeded())) {
      // send for approval
    }
  }
}
