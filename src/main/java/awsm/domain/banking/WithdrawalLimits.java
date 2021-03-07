package awsm.domain.banking;

import static com.google.common.base.Preconditions.checkState;

import awsm.domain.core.Amount;
import java.math.BigDecimal;
import javax.annotation.Nonnull;
import javax.persistence.Embeddable;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.AbstractFactoryBean;
import org.springframework.stereotype.Component;

@Embeddable
@Data
@Accessors(fluent = true)
public class WithdrawalLimits {

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

  @Component
  static class FactoryBean extends AbstractFactoryBean<WithdrawalLimits> {

    private final WithdrawalLimits withdrawalLimits;

    FactoryBean(
        @Value("${banking.account-limits.daily}") BigDecimal dailyLimit,
        @Value("${banking.account-limits.monthly}") BigDecimal monthlyLimit) {
      this.withdrawalLimits = new WithdrawalLimits(Amount.of(dailyLimit), Amount.of(monthlyLimit));
    }

    @Override
    public Class<?> getObjectType() {
      return WithdrawalLimits.class;
    }

    @Override
    @Nonnull
    protected WithdrawalLimits createInstance() {
      return withdrawalLimits;
    }
  }
}
