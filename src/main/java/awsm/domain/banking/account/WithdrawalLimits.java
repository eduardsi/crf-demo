package awsm.domain.banking.account;

import awsm.domain.banking.commons.Amount;
import org.springframework.core.env.Environment;

import static awsm.domain.banking.commons.Amount.amount;
import static com.google.common.base.Preconditions.checkState;

public class WithdrawalLimits {

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

  public static WithdrawalLimits DEFAULTS(Environment env) {
    return new WithdrawalLimits(
            amount(env.getProperty("banking.account-limits.daily")),
            amount(env.getProperty("banking.account-limits.monthly")));
  }

}
