package awsm.application.commons.money;

import java.math.BigDecimal;
import java.math.RoundingMode;
import javax.money.MonetaryAmount;
import javax.money.MonetaryContext;
import javax.money.MonetaryContextBuilder;
import org.javamoney.moneta.Money;

public class Monetary {

  private static MonetaryContext MONETARY_CONTEXT = MonetaryContextBuilder
      .of(MonetaryAmount.class)
      .setMaxScale(2)
      .setPrecision(13)
      .set(RoundingMode.UNNECESSARY)
      .build();

  private Monetary() {
  }

  public static MonetaryAmount amount(String amount) {
    return amount(new BigDecimal(amount));
  }

  public static MonetaryAmount amount(BigDecimal amount) {
    return Money.of(amount, "EUR", MONETARY_CONTEXT);
  }
}
