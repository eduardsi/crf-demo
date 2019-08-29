package awsm.domain.banking;

import awsm.domain.offers.DecimalNumber;
import awsm.infra.hibernate.HibernateConstructor;
import javax.persistence.Embeddable;

@Embeddable
public class WithdrawalLimit {

  private DecimalNumber dailyLimit;

  public WithdrawalLimit(DecimalNumber dailyLimit) {
    this.dailyLimit = dailyLimit;
  }

  @HibernateConstructor
  private WithdrawalLimit() {
  }

  DecimalNumber dailyLimit() {
    return dailyLimit;
  }

}
