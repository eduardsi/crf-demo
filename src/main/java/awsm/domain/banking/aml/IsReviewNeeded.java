package awsm.domain.banking.aml;

import awsm.domain.banking.Transaction;
import awsm.domain.core.Amount;
import awsm.domain.core.Specification;

class IsReviewNeeded implements Specification<Transaction> {

  @Override
  public boolean isSatisfiedBy(Transaction tx) {
    var threshold = Amount.of("1.000.000");
    return tx.withdrawn().isGreaterThanOrEqualTo(threshold);
  }
}
