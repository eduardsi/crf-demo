package awsm.domain.registration;

import static com.google.common.base.Preconditions.checkArgument;

import awsm.domain.DomainException;
import awsm.infra.hibernate.HibernateConstructor;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import org.springframework.stereotype.Component;

@Embeddable
public class Email implements Serializable {

  @Column(unique = true)
  String email;

  public Email(String email) {
    var isNotBlank = email != null && !email.isBlank();
    checkArgument(isNotBlank, "Email %s must not be blank", email);
    this.email = email;
  }

  Email(Email from) {
    this.email = from.toString();
  }

  @HibernateConstructor
  private Email() {

  }

  @Override
  public final String toString() {
    return email;
  }

  @Override
  public final int hashCode() {
    return Objects.hashCode(email);
  }

  @Override
  public final boolean equals(Object obj) {
    if (obj instanceof Email) {
      Email that = (Email) obj;
      return this.email.equals(that.email);
    }
    return false;
  }

  @Embeddable
  public static class Unique extends Email {

    public Unique(Uniqueness uniqueness, Email email) {
      super(email);
      if (!uniqueness.guaranteed(this)) {
        throw new NotUniqueException(this);
      }
    }

    @HibernateConstructor
    private Unique() {
    }

  }

  static class NotUniqueException extends DomainException {
    NotUniqueException(Email email) {
      super("Email " + email + " is not unique");
    }
  }


  @Embeddable
  public static class NotBlacklisted extends Email {

    public NotBlacklisted(Blacklist blacklist, Unique email) {
      super(email);
      if (!blacklist.allows(email)) {
        throw new BlacklistedException(email);
      }
    }

    @HibernateConstructor
    private NotBlacklisted() {
    }

  }

  static class BlacklistedException extends DomainException {
    BlacklistedException(Email email) {
      super("Email " + email + " is blacklisted");
    }
  }


  public interface Uniqueness {

    boolean guaranteed(Email email);

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

  }

  public interface Blacklist {

    boolean allows(Email email);

    @Component
    class HardCoded implements Blacklist {

      private static final List<String> BAD_DOMAINS = List.of("pornhub.com", "rotten.com");

      @Override
      public boolean allows(Email email) {
        return BAD_DOMAINS
            .stream()
            .noneMatch(domain -> email.toString().contains(domain));
      }

    }

  }
}
