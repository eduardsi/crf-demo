package awsm.domain.banking;

import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
class SendTransactionForManualApprovalOnWithdrawal {

  private final BankAccountRepository accounts;

  SendTransactionForManualApprovalOnWithdrawal(BankAccountRepository accounts) {
    this.accounts = accounts;
  }

  @TransactionalEventListener
  public void trigger(WithdrawalHappened event) {
    var account = accounts.getOne(event.iban());
    var txUid = event.txUid();
    var tx = account.tx(txUid);

    if (tx.satisfies(new IsManualApprovalNeeded())) {
      // send for approval
    }
  }
}
