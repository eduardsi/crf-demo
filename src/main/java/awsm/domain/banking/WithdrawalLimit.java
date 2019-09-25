package awsm.domain.banking;

import awsm.domain.offers.$;
import awsm.infra.hibernate.HibernateConstructor;
import javax.persistence.Embeddable;

@Embeddable
public class WithdrawalLimit {

  private $ dailyLimit;

  public WithdrawalLimit($ dailyLimit) {
    this.dailyLimit = dailyLimit;
  }

  @HibernateConstructor
  private WithdrawalLimit() {
  }

  $ dailyLimit() {
    return dailyLimit;
  }

}
