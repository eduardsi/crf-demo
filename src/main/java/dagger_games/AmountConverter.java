package dagger_games;

import org.jooq.Converter;

import java.math.BigDecimal;

public class AmountConverter implements Converter<BigDecimal, Amount> {

    @Override
    public BigDecimal to(Amount obj) {
        return obj.toDecimal();
    }

    @Override
    public Amount from(BigDecimal obj) {
        return Amount.of(obj);
    }

    @Override
    public Class<BigDecimal> fromType() {
        return BigDecimal.class;
    }

    @Override
    public Class<Amount> toType() {
        return Amount.class;
    }
}
