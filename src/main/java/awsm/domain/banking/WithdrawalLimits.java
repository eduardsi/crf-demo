package awsm.domain.banking;

import static com.google.common.base.Preconditions.checkState;

import awsm.domain.core.Amount;
import javax.persistence.Embeddable;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.core.env.Environment;

@Embeddable
@Data
@Accessors(fluent = true)
public class WithdrawalLimits {

  private Amount dailyLimit;

  private Amount monthlyLimit;

  public WithdrawalLimits(Environment env) {
    this(
        Amount.of(env.getProperty("banking.account-limits.daily")),
        Amount.of(env.getProperty("banking.account-limits.monthly")));
  }

  public WithdrawalLimits(Amount dailyLimit, Amount monthlyLimit) {
    var withinLimits = monthlyLimit.isGreaterThanOrEqualTo(dailyLimit);
    checkState(
        withinLimits,
        "Monthly limit (%s) must be higher or equal to daily limit (%s)",
        monthlyLimit,
        dailyLimit);

    this.dailyLimit = dailyLimit;
    this.monthlyLimit = monthlyLimit;
  }

  private WithdrawalLimits() {}
}
