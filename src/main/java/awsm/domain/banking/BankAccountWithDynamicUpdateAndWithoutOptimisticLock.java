package awsm.domain.banking;

import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "BANK_ACCOUNT")
@DynamicUpdate
public class BankAccountWithDynamicUpdateAndWithoutOptimisticLock extends BaseBankAccount<BankAccountWithDynamicUpdateAndWithoutOptimisticLock> {

    public BankAccountWithDynamicUpdateAndWithoutOptimisticLock(AccountHolder holder, WithdrawalLimits withdrawalLimits) {
        super(holder, withdrawalLimits);
    }

    BankAccountWithDynamicUpdateAndWithoutOptimisticLock() {
        super();
    }

}

