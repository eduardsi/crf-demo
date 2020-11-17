package awsm.domain.banking;


import awsm.domain.core.Specification;
import awsm.domain.core.Amount;

public class IsManualApprovalNeeded implements Specification<Transaction> {

    @Override
    public boolean isSatisfiedBy(Transaction tx) {
        var threshold = Amount.of("1.000.000");
        return tx.deposited().isGreaterThanOrEqualTo(threshold) || tx.withdrawn().isGreaterThanOrEqualTo(threshold);
    }
}
