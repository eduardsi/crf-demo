package awsm.domain.banking;

import awsm.infrastructure.data.Data;

import java.math.BigDecimal;
import java.math.RoundingMode;

class Amount extends Data {

  private final BigDecimal decimal;

  private Amount(BigDecimal decimal) {
    this.decimal = decimal.setScale(2, RoundingMode.UNNECESSARY);
  }

  Amount add(Amount other) {
    return new Amount(decimal.add(other.decimal));
  }

  Amount subtract(Amount other) {
    return new Amount(decimal.subtract(other.decimal));
  }

  Amount abs() {
    return new Amount(decimal.abs());
  }

  boolean isGreaterThan(Amount other) {
    return decimal.compareTo(other.decimal) > 0;
  }

  boolean isGreaterThanOrEqualTo(Amount other) {
    return decimal.compareTo(other.decimal) >= 0;
  }

  boolean isLessThanOrEqualTo(Amount other) {
    return decimal.compareTo(other.decimal) <= 0;
  }

  public BigDecimal decimal() {
    return decimal;
  }

  @Override
  public String toString() {
    return decimal.toString();
  }

  static Amount amount(String amount) {
    return amount(new BigDecimal(amount));
  }

  static Amount amount(BigDecimal amount) {
    return new Amount(amount);
  }
}