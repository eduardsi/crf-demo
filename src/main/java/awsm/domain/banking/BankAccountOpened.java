package awsm.domain.banking;

import awsm.domain.core.DomainEvent;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.annotation.Nullable;

public class BankAccountOpened implements DomainEvent {

    private final String iban;

    BankAccountOpened(String iban) {
        this.iban = iban;
    }

    public String iban() {
        return iban;
    }

    @Override
    public final boolean equals(@Nullable Object that) {
        return EqualsBuilder.reflectionEquals(this, that);
    }

    @Override
    public final int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

}
