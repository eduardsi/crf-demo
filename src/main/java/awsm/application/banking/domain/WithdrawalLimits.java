package awsm.application.banking.domain;

import static com.google.common.base.Preconditions.checkState;

import javax.money.MonetaryAmount;

class WithdrawalLimits {

  private final MonetaryAmount dailyLimit;
  private final MonetaryAmount monthlyLimit;

  public WithdrawalLimits(MonetaryAmount dailyLimit, MonetaryAmount monthlyLimit) {
    var limitsAreOk = monthlyLimit.isGreaterThan(dailyLimit);
    checkState(limitsAreOk, "Monthly limit (%s) must be higher than daily limit (%s)",
        monthlyLimit.getNumber(), dailyLimit.getNumber());

    this.dailyLimit = dailyLimit;
    this.monthlyLimit = monthlyLimit;
  }

  MonetaryAmount dailyLimit() {
    return dailyLimit;
  }

  MonetaryAmount monthlyLimit() {
    return monthlyLimit;
  }


}
