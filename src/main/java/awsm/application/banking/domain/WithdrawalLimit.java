package awsm.application.banking.domain;

import javax.money.MonetaryAmount;

class WithdrawalLimit {

  private MonetaryAmount dailyLimit;

  public WithdrawalLimit(MonetaryAmount dailyLimit) {
    this.dailyLimit = dailyLimit;
  }

  MonetaryAmount dailyLimit() {
    return dailyLimit;
  }

}
