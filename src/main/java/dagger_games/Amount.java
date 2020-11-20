package dagger_games;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Amount {

    public static final Amount ZERO = new Amount(BigDecimal.ZERO);

    private final BigDecimal decimal;

    private Amount(BigDecimal decimal) {
        this.decimal = decimal.setScale(2, RoundingMode.UNNECESSARY);
    }

    private Amount(String decimal) {
        this(new BigDecimal(decimal));
    }

    public static Amount of(BigDecimal decimal) {
        return new Amount(decimal);
    }

    public static Amount of(String decimal) {
        return new Amount(decimal);
    }

    public Amount abs() {
        return new Amount(this.decimal.abs());
    }

    public Amount add(Amount that) {
        return new Amount(this.decimal.add(that.decimal));
    }

    public Amount subtract(Amount that) {
        return new Amount(this.decimal.subtract(that.decimal));
    }

    public boolean isGreaterThan(Amount dailyLimit) {
        return this.decimal.compareTo(dailyLimit.decimal) > 0;
    }

    public boolean isGreaterThanOrEqualTo(Amount that) {
        return this.decimal.compareTo(that.decimal) >= 0;
    }

    public boolean isPositive() {
        return isGreaterThanOrEqualTo(ZERO);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Amount) {
            var that = (Amount) obj;
            return this.decimal.equals(that.decimal);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return decimal.hashCode();
    }

    @Override
    public String toString() {
        return decimal.toString();
    }

    public BigDecimal toDecimal() {
        return decimal;
    }

}