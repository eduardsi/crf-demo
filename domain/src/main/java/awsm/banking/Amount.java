package awsm.banking;

import static java.math.BigDecimal.ZERO;

import java.math.BigDecimal;
import java.math.RoundingMode;

class Amount {

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

  boolean isNegative() {
    return decimal.compareTo(ZERO) < 0;
  }

  boolean isNegativeOrZero() {
    return decimal.compareTo(ZERO) <= 0;
  }

  boolean izZero() {
    return decimal.compareTo(ZERO) == 0;
  }

  boolean isPositive() {
    return decimal.compareTo(ZERO) > 0;
  }

  boolean isPositiveOrZero() {
    return decimal.compareTo(ZERO) >= 0;
  }

  boolean isEqualTo(Amount other) {
    return decimal.compareTo(other.decimal) == 0;
  }

  boolean isGreaterThan(Amount other) {
    return decimal.compareTo(other.decimal) > 0;
  }

  boolean isGreaterThanOrEqualTo(Amount other) {
    return decimal.compareTo(other.decimal) >= 0;
  }

  boolean isLessThan(Amount other) {
    return decimal.compareTo(other.decimal) < 0;
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

  @Override
  public int hashCode() {
    return decimal.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof Amount that) {
      return decimal.equals(that.decimal());
    } else {
      return false;
    }
  }

  static Amount amount(String amount) {
    return amount(new BigDecimal(amount));
  }

  static Amount amount(BigDecimal amount) {
    return new Amount(amount);
  }
}