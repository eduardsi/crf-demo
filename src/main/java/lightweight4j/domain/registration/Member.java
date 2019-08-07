package lightweight4j.domain.registration;

import lightweight4j.infra.hibernate.HibernateEntity;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Table;

import static java.util.Objects.requireNonNull;

@Entity
@Table(name = "members")
public class Member extends HibernateEntity {

    @Embedded
    private Email email;

    @Embedded
    private Name name;

    public Member(Name name, Email email) {
        this.name = requireNonNull(name, "Name cannot be null");
        this.email = requireNonNull(email, "Email cannot be null");
        events().schedule(new RegistrationCompleted(this::id));
    }

    private Member() {
    }

    public Name name() {
        return name;
    }

    public Email email() {
        return email;
    }
}
