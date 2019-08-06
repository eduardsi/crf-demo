package lightweight4j.domain.registration;

import javax.persistence.Embeddable;

import static com.google.common.base.Preconditions.checkArgument;

@Embeddable
public class Email {

    private String email;

    public Email(String email) {
        var isNotBlank = email != null && !email.isBlank();
        checkArgument(isNotBlank, "Email %s must not be blank", email);
        this.email = email;
    }

    private Email() {
    }

    @Override
    public String toString() {
        return email;
    }

}
