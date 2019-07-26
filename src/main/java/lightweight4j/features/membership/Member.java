package lightweight4j.features.membership;

import lightweight4j.lib.domain.DomainEntity;
import lightweight4j.lib.hibernate.HibernateConstructor;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "members")
class Member extends DomainEntity {

    @Embedded
    private Email email;

    @Embedded
    private Name name;

    public Member(Name name, Email email) {
        this.name = name;
        this.email = email;
        schedule(new MemberHasArrived(this::id));
    }

    @HibernateConstructor
    private Member() {
    }

    public Email email() {
        return email;
    }

}
