package awsm.domain.registration;

import static com.google.common.base.Preconditions.checkArgument;

import awsm.infra.hibernate.HibernateConstructor;
import java.io.Serializable;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import org.springframework.stereotype.Component;

@Embeddable
public class Email implements Serializable {

  @Column(unique = true)
  private String email;

  public Email(String email) {
    var isNotBlank = email != null && !email.isBlank();
    checkArgument(isNotBlank, "Email %s must not be blank", email);
    this.email = email;
  }

  @HibernateConstructor
  private Email() {
  }

  @Override
  public final String toString() {
    return email;
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(email);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof Email) {
      Email that = (Email) obj;
      return this.email.equals(that.email);
    }
    return false;
  }

  @Embeddable
  public static class Unique extends Email {

    public Unique(String email, Uniqueness uniqueness) {
      super(email);
      var e = new Email(email);
      if (!uniqueness.guaranteed(e)) {
        throw new EmailNotUniqueException(e);
      }
    }

    @HibernateConstructor
    private Unique() {
    }

  }

  public interface Uniqueness {

    boolean guaranteed(Email email);

    default Uniqueness memoized() {
      return new Memoized(this);
    }

    @Component
    class AcrossMembers implements Uniqueness {

      private final Members members;

      private AcrossMembers(Members members) {
        this.members = members;
      }

      @Override
      public boolean guaranteed(Email email) {
        return members.findByEmail(email).isEmpty();
      }

    }

    class Memoized implements Email.Uniqueness {

      private final Email.Uniqueness origin;
      private final ConcurrentHashMap<Email, Boolean> memoizer = new ConcurrentHashMap<>();

      Memoized(Email.Uniqueness origin) {
        this.origin = origin;
      }

      @Override
      public boolean guaranteed(Email email) {
        return memoizer.computeIfAbsent(email, origin::guaranteed);
      }
    }

  }
}
