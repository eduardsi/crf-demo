package awsm.domain.offers;

import java.math.BigDecimal;
import java.math.RoundingMode;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

public interface $ {

  $ ZERO = new Const(BigDecimal.ZERO);

  default $ add($ other) {
    var inc = this.big().add(other.big());
    return new Const(inc);
  }

  default $ subtract($ other) {
    var dec = this.big().subtract(other.big());
    return new Const(dec);
  }

  default $ abs() {
    return new Const(this.big().abs());
  }

  default boolean isGe($ other) {
    return this.big().compareTo(other.big()) >= 0;
  }

  BigDecimal big();

  static $ of(BigDecimal decimal) {
    return new Const(decimal);
  }

  static $ of(String decimal) {
    return new Const(decimal);
  }

  class Const implements $ {

    private final BigDecimal decimal;

    private Const(BigDecimal decimal) {
      this.decimal = decimal.setScale(2, RoundingMode.UNNECESSARY);
    }

    private Const(String decimal) {
      this(new BigDecimal(decimal));
    }

    public BigDecimal big() {
      return this.decimal;
    }

    @Override
    public String toString() {
      return big().toString();
    }

    @Override
    public int hashCode() {
      return big().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
      if (obj instanceof Const) {
        var that = (Const) obj;
        return this.big().equals(that.big());
      }
      return false;
    }

    @Converter(autoApply = true)
    public static class ToBigDecimal implements AttributeConverter<$, BigDecimal> {

      @Override
      public BigDecimal convertToDatabaseColumn($ decimal) {
        return decimal.big();
      }

      @Override
      public Const convertToEntityAttribute(BigDecimal decimal) {
        return new Const(decimal);
      }

    }
  }
}
