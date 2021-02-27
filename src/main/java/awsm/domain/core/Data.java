package awsm.domain.core;

import javax.annotation.Nullable;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public abstract class Data {

  @Override
  public final boolean equals(@Nullable Object that) {
    return EqualsBuilder.reflectionEquals(this, that);
  }

  @Override
  public final int hashCode() {
    return HashCodeBuilder.reflectionHashCode(this);
  }

  @Override
  public final String toString() {
    return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
  }
}
