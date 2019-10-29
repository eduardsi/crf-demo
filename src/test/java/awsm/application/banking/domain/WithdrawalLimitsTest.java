package awsm.application.banking.domain;

import static awsm.application.commons.money.Monetary.amount;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("withdrawal limits")
class WithdrawalLimitsTest {

  @Test
  void make_sure_that_monthly_limit_is_always_higher_than_daily_limit() {
    // ok
    new WithdrawalLimits(amount("100.00"), amount("101.00"));

    // nok
    var e = assertThrows(IllegalStateException.class, () ->
        new WithdrawalLimits(amount("100.00"), amount("100.00"))
    );
    assertThat(e).hasMessage("Monthly limit (100.00) must be higher than daily limit (100.00)");

    // nok
    var e2 = assertThrows(IllegalStateException.class, () ->
        new WithdrawalLimits(amount("100.00"), amount("99.00"))
    );
    assertThat(e2).hasMessage("Monthly limit (99.00) must be higher than daily limit (100.00)");
  }


}