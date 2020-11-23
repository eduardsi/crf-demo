package dagger_games;

import com.google.auto.factory.AutoFactory;
import com.google.auto.factory.Provided;
import com.typesafe.config.Config;

import javax.inject.Inject;

@AutoFactory
public class WithdrawalLimits {

    public final Amount dailyLimit;
    public final Amount monthlyLimit;

    WithdrawalLimits(Amount dailyLimit, Amount monthlyLimit) {
        this.dailyLimit = dailyLimit;
        this.monthlyLimit = monthlyLimit;
    }

    @Inject
    public WithdrawalLimits(@Provided Config config) {
        this(
                Amount.of(config.getString("banking.account-limits.daily")),
                Amount.of(config.getString("banking.account-limits.monthly")));
    }
}
