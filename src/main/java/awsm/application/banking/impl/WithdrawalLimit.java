package awsm.application.banking.impl;

import awsm.application.trading.impl.$;

public class WithdrawalLimit {

  private $ dailyLimit;

  public WithdrawalLimit($ dailyLimit) {
    this.dailyLimit = dailyLimit;
  }

  $ dailyLimit() {
    return dailyLimit;
  }

}
