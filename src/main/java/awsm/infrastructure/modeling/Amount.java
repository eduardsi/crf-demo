package awsm.infrastructure.modeling;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Amount {

  public static final Amount ZERO = Amount.of(BigDecimal.ZERO);

  private final BigDecimal value;

  private Amount(BigDecimal decimal) {
    this.value = decimal.setScale(2, RoundingMode.UNNECESSARY);
  }

  private Amount(String decimal) {
    this(new BigDecimal(decimal));
  }

  public Amount add(Amount other) {
    var inc = value.add(other.value);
    return Amount.of(inc);
  }

  public Amount subtract(Amount other) {
    var dec = value.subtract(other.value);
    return Amount.of(dec);
  }

  public Amount abs() {
    return Amount.of(value.abs());
  }

  public boolean isAtLeast(Amount other) {
    return value.compareTo(other.value) >= 0;
  }

  public boolean isAtMost(Amount other) {
    return value.compareTo(other.value) <= 0;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof Amount) {
      var that = (Amount) obj;
      return this.value.equals(that.value);
    }
    return false;
  }

  @Override
  public int hashCode() {
    return value.hashCode();
  }

  @Override
  public String toString() {
    return value.toString();
  }

  public BigDecimal toBigDecimal() {
    return value;
  }

  public static Amount of(BigDecimal decimal) {
    return new Amount(decimal);
  }

  public static Amount of(String decimal) {
    return new Amount(decimal);
  }

}
