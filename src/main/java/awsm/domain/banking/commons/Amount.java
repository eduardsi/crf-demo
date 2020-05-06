package awsm.domain.banking.commons;

import awsm.infrastructure.data.Data;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Amount extends Data {

  private final BigDecimal decimal;

  private Amount(BigDecimal decimal) {
    this.decimal = decimal.setScale(2, RoundingMode.UNNECESSARY);
  }

  public Amount add(Amount other) {
    return new Amount(decimal.add(other.decimal));
  }

  public Amount subtract(Amount other) {
    return new Amount(decimal.subtract(other.decimal));
  }

  public Amount abs() {
    return new Amount(decimal.abs());
  }

  public boolean isGreaterThan(Amount other) {
    return decimal.compareTo(other.decimal) > 0;
  }

  public boolean isGreaterThanOrEqualTo(Amount other) {
    return decimal.compareTo(other.decimal) >= 0;
  }

  public boolean isLessThanOrEqualTo(Amount other) {
    return decimal.compareTo(other.decimal) <= 0;
  }

  public BigDecimal decimal() {
    return decimal;
  }

  @Override
  public String toString() {
    return decimal.toString();
  }

  public static Amount amount(String amount) {
    return amount(new BigDecimal(amount));
  }

  public static Amount amount(BigDecimal amount) {
    return new Amount(amount);
  }
}