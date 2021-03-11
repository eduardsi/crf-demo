package awsm.domain.banking.aml;

import awsm.domain.banking.BankAccountRepository;
import awsm.domain.banking.WithdrawalHappened;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
class SendTransactionForReviewOnWithdrawal {

  private final BankAccountRepository accounts;
  private final TransactionsForReviewRepository txForReviewRepository;

  SendTransactionForReviewOnWithdrawal(
      BankAccountRepository accounts, TransactionsForReviewRepository txForReviewRepository) {
    this.accounts = accounts;
    this.txForReviewRepository = txForReviewRepository;
  }

  @TransactionalEventListener
  public void trigger(WithdrawalHappened event) {
    var account = accounts.getOne(event.iban());
    var txUid = event.txUid();
    var tx = account.tx(txUid);

    if (tx.satisfies(new IsReviewNeeded())) {
      var txForReview = new TransactionForReview(tx.uid(), tx.withdrawn(), event.iban());
      txForReviewRepository.save(txForReview);
    }
  }
}
