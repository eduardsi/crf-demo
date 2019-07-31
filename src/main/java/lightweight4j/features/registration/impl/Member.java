package lightweight4j.features.registration.impl;

import lightweight4j.features.registration.RegistrationCompleted;
import lightweight4j.lib.domain.DomainEntity;
import lightweight4j.lib.hibernate.HibernateConstructor;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "members")
class Member extends DomainEntity {

    @Embedded
    public Email email;

    @Embedded
    public Name name;

    public Member(Name name, Email email) {
        this.name = name;
        this.email = email;
        schedule(new RegistrationCompleted(this::id));
    }

    @HibernateConstructor
    private Member() {
    }

}
