package lightweight4j.features.registration.impl;

import lightweight4j.features.registration.RegistrationCompleted;
import lightweight4j.lib.hibernate.HibernateEntity;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Table;

import static java.util.Objects.requireNonNull;

@Entity
@Table(name = "members")
class Member extends HibernateEntity {

    @Embedded
    Email email;

    @Embedded
    Name name;

    Member(Name name, Email email) {
        this.name = requireNonNull(name, "Name cannot be null");
        this.email = requireNonNull(email, "Email cannot be null");
        schedule(new RegistrationCompleted(this::id));
    }

    private Member() {
    }

}
