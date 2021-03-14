package awsm.domain.banking.aml;

import awsm.domain.banking.Transaction;
import awsm.domain.core.Amount;
import awsm.domain.core.Specification;

public class IsManualReviewNeeded implements Specification<Transaction> {

  @Override
  public boolean isSatisfiedBy(Transaction tx) {
    var threshold = Amount.of("1.000.000");
    return tx.withdrawn().isGreaterThanOrEqualTo(threshold);
  }
}
