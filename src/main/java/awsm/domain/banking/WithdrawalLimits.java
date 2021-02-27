package awsm.domain.banking;

import static com.google.common.base.Preconditions.checkState;

import awsm.domain.core.Amount;
import awsm.domain.core.Data;
import java.math.BigDecimal;
import javax.persistence.Embeddable;
import org.springframework.core.env.Environment;

@Embeddable
public class WithdrawalLimits extends Data {

  private Amount dailyLimit;

  private Amount monthlyLimit;

  public WithdrawalLimits(Amount dailyLimit, Amount monthlyLimit) {
    var withinLimits = monthlyLimit.isGreaterThan(dailyLimit);
    checkState(
        withinLimits,
        "Monthly limit (%s) must be higher than daily limit (%s)",
        monthlyLimit,
        dailyLimit);

    this.dailyLimit = dailyLimit;
    this.monthlyLimit = monthlyLimit;
  }

  private WithdrawalLimits() {}

  Amount dailyLimit() {
    return dailyLimit;
  }

  Amount monthlyLimit() {
    return monthlyLimit;
  }

  public static WithdrawalLimits defaults(Environment env) {
    return new WithdrawalLimits(
        Amount.of(env.getProperty("banking.account-limits.daily", BigDecimal.class)),
        Amount.of(env.getProperty("banking.account-limits.monthly", BigDecimal.class)));
  }
}
