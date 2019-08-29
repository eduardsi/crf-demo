package awsm.domain.banking;

import awsm.domain.offers.DecimalNumber;
import awsm.infra.hibernate.HibernateConstructor;
import awsm.infra.media.Media;
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

  void printTo(Media media) {
    media.print("dailyLimit", dailyLimit.toString());
  }
}
