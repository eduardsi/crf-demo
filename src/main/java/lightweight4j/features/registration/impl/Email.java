package lightweight4j.features.registration.impl;

import javax.persistence.Embeddable;

import java.util.Objects;

import static com.google.common.base.Preconditions.checkArgument;

@Embeddable
class Email {

    private String email;

    Email(String email) {
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
