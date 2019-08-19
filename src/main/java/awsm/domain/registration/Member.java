package awsm.domain.registration;

import static java.util.Objects.requireNonNull;

import awsm.infra.hibernate.HibernateConstructor;
import javax.annotation.Nullable;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import org.hibernate.annotations.NaturalId;

@Entity
@Table(name = "members")
public class Member {

  @Id
  @GeneratedValue
  @Nullable
  private Long id;

  @Embedded
  @NaturalId
  private Email email;

  @Embedded
  private Name name;

  public Member(Name name, Email.Unique email) {
    this.name = requireNonNull(name, "Name cannot be null");
    this.email = requireNonNull(email, "Email cannot be null");
    new Registration(this).schedule();
  }

  @HibernateConstructor
  private Member() {
  }

  public Name name() {
    return name;
  }

  public Email email() {
    return email;
  }

  public Long id() {
    return requireNonNull(id, "ID is null. Perhaps the entity has not been persisted yet?");
  }


}
