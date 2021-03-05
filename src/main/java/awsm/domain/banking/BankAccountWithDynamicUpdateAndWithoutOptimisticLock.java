package awsm.domain.banking;

import javax.persistence.Entity;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@DynamicUpdate
public class BankAccountWithDynamicUpdateAndWithoutOptimisticLock
    extends BaseBankAccount<BankAccountWithDynamicUpdateAndWithoutOptimisticLock> {

  public BankAccountWithDynamicUpdateAndWithoutOptimisticLock(
      AccountHolder holder, WithdrawalLimits withdrawalLimits) {
    super(holder, withdrawalLimits);
  }

  BankAccountWithDynamicUpdateAndWithoutOptimisticLock() {
    super();
  }
}
