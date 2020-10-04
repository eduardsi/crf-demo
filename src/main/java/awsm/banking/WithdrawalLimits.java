package awsm.banking;

import org.springframework.core.env.Environment;

import javax.persistence.Embeddable;
import java.math.BigDecimal;

import static com.google.common.base.Preconditions.checkState;

@Embeddable
public class WithdrawalLimits {

  private BigDecimal dailyLimit;

  private BigDecimal monthlyLimit;

  public WithdrawalLimits(BigDecimal dailyLimit, BigDecimal monthlyLimit) {
    var withinLimits = monthlyLimit.compareTo(dailyLimit) > 0;
    checkState(withinLimits, "Monthly limit (%s) must be higher than daily limit (%s)", monthlyLimit, dailyLimit);

    this.dailyLimit = dailyLimit;
    this.monthlyLimit = monthlyLimit;
  }

  private WithdrawalLimits() {
  }

  BigDecimal dailyLimit() {
    return dailyLimit;
  }

  BigDecimal monthlyLimit() {
    return monthlyLimit;
  }

  public static WithdrawalLimits defaults(Environment env) {
    return new WithdrawalLimits(
            env.getProperty("banking.account-limits.daily", BigDecimal.class),
            env.getProperty("banking.account-limits.monthly", BigDecimal.class));
  }

}
