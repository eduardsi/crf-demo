package awsm.application.banking.impl;

import awsm.infrastructure.modeling.Amount;

public class WithdrawalLimit {

  private Amount dailyLimit;

  public WithdrawalLimit(Amount dailyLimit) {
    this.dailyLimit = dailyLimit;
  }

  Amount dailyLimit() {
    return dailyLimit;
  }

}
