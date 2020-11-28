package awsm.domain.banking;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "BANK_ACCOUNT")
public class BankAccountWithoutOptimisticLock extends BaseBankAccount<BankAccountWithoutOptimisticLock> {

    public BankAccountWithoutOptimisticLock(AccountHolder holder, WithdrawalLimits withdrawalLimits) {
        super(holder, withdrawalLimits);
    }

    BankAccountWithoutOptimisticLock() {
        super();
    }

}

