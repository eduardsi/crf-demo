package awsm.domain.banking;

import awsm.domain.core.DomainEvent;
import org.springframework.stereotype.Component;

@Component
class SendTransactionForManualApprovalOnWithdrawal
    implements DomainEvent.SideEffect<WithdrawalHappened> {

  private final BankAccountRepository accounts;

  SendTransactionForManualApprovalOnWithdrawal(BankAccountRepository accounts) {
    this.accounts = accounts;
  }

  @Override
  public void trigger(WithdrawalHappened event) {
    var account = accounts.getOne(event.iban());
    var txUid = event.txUid();
    var tx = account.tx(txUid);

    if (tx.satisfies(new IsManualApprovalNeeded())) {
      // send for approval
    }
  }
}
