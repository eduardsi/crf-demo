package awsm.domain.banking;

import javax.persistence.Entity;
import javax.persistence.Table;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Table(name = "BANK_ACCOUNT")
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
