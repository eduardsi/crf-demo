package lightweight4j.features.registration.impl;

import lightweight4j.features.registration.RegistrationCompleted;
import lightweight4j.lib.hibernate.HibernateEntity;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "members")
class Member extends HibernateEntity {

    @Embedded
    Email email;

    @Embedded
    Name name;

    Member(Name name, Email email) {
        this.name = name;
        this.email = email;
        schedule(new RegistrationCompleted(this::id));
    }

    private Member() {
    }

}
