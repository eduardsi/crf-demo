package lightweight4j.features.membership;

import lightweight4j.lib.hibernate.HibernateConstructor;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.UUID;

@Entity(name = "Members")
class Member {

    @Id
    private String id = UUID.randomUUID().toString();

    @Embedded
    private Email email;

    @Embedded
    private Name name;

    public Member(Name name, Email email) {
        this.name = name;
        this.email = email;
    }

    @HibernateConstructor
    private Member() {
    }



    public Email email() {
        return email;
    }

    public String id() {
        return id;
    }
}
