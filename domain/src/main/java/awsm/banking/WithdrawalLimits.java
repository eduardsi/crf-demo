package awsm.banking;

import static com.google.common.base.Preconditions.checkState;

class WithdrawalLimits {

  private final Amount dailyLimit;
  private final Amount monthlyLimit;

  public WithdrawalLimits(Amount dailyLimit, Amount monthlyLimit) {
    var limitsAreOk = monthlyLimit.isGreaterThan(dailyLimit);
    checkState(limitsAreOk, "Monthly limit (%s) must be higher than daily limit (%s)", monthlyLimit, dailyLimit);

    this.dailyLimit = dailyLimit;
    this.monthlyLimit = monthlyLimit;
  }

  Amount dailyLimit() {
    return dailyLimit;
  }

  Amount monthlyLimit() {
    return monthlyLimit;
  }


}
