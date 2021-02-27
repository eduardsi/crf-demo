package awsm.domain.banking;

import javax.persistence.Entity;
import javax.persistence.Version;

@Entity
public class BankAccount extends BaseBankAccount<BankAccount> {

  @Version private long version;

  public BankAccount(AccountHolder holder, WithdrawalLimits withdrawalLimits) {
    super(holder, withdrawalLimits);
  }

  BankAccount() {}
}
