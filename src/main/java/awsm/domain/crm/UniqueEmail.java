package awsm.domain.crm;

import com.google.common.base.Strings;
import org.apache.commons.lang3.builder.EqualsBuilder;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import java.util.Objects;

import static com.google.common.base.Preconditions.checkArgument;

@Embeddable
public class UniqueEmail {

    @Column(unique = true)
    private String email;

    public UniqueEmail(String email, Uniqueness uniqueness) {
        var isNotEmpty = !Strings.isNullOrEmpty(email);
        checkArgument(isNotEmpty, "Email %s must not be blank", email);
        checkArgument(uniqueness.guaranteed(email), "Email %s is not unique", email);
        this.email = email;
    }

    private UniqueEmail() {
    }

    @Override
    public String toString() {
        return email;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof UniqueEmail) {
            var that = (UniqueEmail) obj;
            return this.email.equals(that.email);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(email);
    }

}
