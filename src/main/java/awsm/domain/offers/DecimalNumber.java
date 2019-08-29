package awsm.domain.offers;

import java.math.BigDecimal;
import java.math.RoundingMode;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

public class DecimalNumber {

  public static final DecimalNumber ZERO = new DecimalNumber(BigDecimal.ZERO);

  private final BigDecimal decimal;

  public DecimalNumber(BigDecimal decimal) {
    this.decimal = decimal.setScale(2, RoundingMode.UNNECESSARY);
  }

  public DecimalNumber(String decimal) {
    this(new BigDecimal(decimal));
  }

  DecimalNumber multiply(DecimalNumber other) {
    var mult = this.decimal.multiply(other.decimal);
    return new DecimalNumber(mult);
  }

  public DecimalNumber plus(DecimalNumber other) {
    var inc = this.decimal.add(other.decimal);
    return new DecimalNumber(inc);
  }

  public DecimalNumber minus(DecimalNumber other) {
    var dec = this.decimal.subtract(other.decimal);
    return new DecimalNumber(dec);
  }

  public DecimalNumber abs() {
    return new DecimalNumber(this.decimal.abs());
  }

  public boolean isEqualOrGreaterThan(DecimalNumber other) {
    return this.decimal.compareTo(other.decimal) >= 0;
  }

  public BigDecimal asDecimal() {
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
    if (obj instanceof DecimalNumber) {
      var that = (DecimalNumber) obj;
      return this.decimal.equals(that.decimal);
    }
    return false;
  }

  @Converter(autoApply = true)
  public static class ToBigDecimal implements AttributeConverter<DecimalNumber, BigDecimal> {

    @Override
    public BigDecimal convertToDatabaseColumn(DecimalNumber decimalNumber) {
      return decimalNumber.decimal;
    }

    @Override
    public DecimalNumber convertToEntityAttribute(BigDecimal decimal) {
      return new DecimalNumber(decimal);
    }
  }
}
