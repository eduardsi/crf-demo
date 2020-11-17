package awsm.domain.banking;

import awsm.domain.core.DomainEvent;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.annotation.Nullable;
import java.util.UUID;

public class WithdrawalHappened implements DomainEvent {

    private final String iban;
    private final UUID txUid;

    public WithdrawalHappened(String iban, UUID txUid) {
        this.iban = iban;
        this.txUid = txUid;
    }

    String iban() {
        return iban;
    }

    UUID txUid() {
        return txUid;
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
