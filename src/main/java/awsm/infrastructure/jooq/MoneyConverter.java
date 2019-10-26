package awsm.infrastructure.jooq;

import awsm.application.commons.money.Monetary;
import java.math.BigDecimal;
import javax.money.MonetaryAmount;
import org.jooq.Converter;

public class MoneyConverter implements Converter<BigDecimal, MonetaryAmount> {

  @Override
  public MonetaryAmount from(BigDecimal decimal) {
    return Monetary.amount(decimal);
  }

  @Override
  public BigDecimal to(MonetaryAmount monetary) {
    return monetary.getNumber().numberValue(BigDecimal.class);
  }

  @Override
  public Class<BigDecimal> fromType() {
    return BigDecimal.class;
  }

  @Override
  public Class<MonetaryAmount> toType() {
    return MonetaryAmount.class;
  }
}
