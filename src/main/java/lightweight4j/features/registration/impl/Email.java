package lightweight4j.features.registration.impl;

import lightweight4j.lib.hibernate.HibernateConstructor;

import javax.persistence.Embeddable;

import java.util.Objects;

import static com.google.common.base.Preconditions.checkArgument;

@Embeddable
class Email {

    private String email;

    public Email(String email) {
        var isNotBlank = !Objects.toString(email).isBlank();
        checkArgument(isNotBlank, "Email %s must not be blank", email);
        this.email = email;
    }

    @HibernateConstructor
    private Email() {
    }

    @Override
    public String toString() {
        return email;
    }

}
