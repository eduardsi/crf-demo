package awsm.domain.registration;

import static com.google.common.base.Preconditions.checkArgument;
import static java.net.http.HttpClient.newHttpClient;
import static java.net.http.HttpResponse.BodyHandlers.ofString;
import static java.time.Duration.ofSeconds;

import awsm.domain.DomainException;
import awsm.infra.hibernate.HibernateConstructor;
import java.io.Serializable;
import java.net.URI;
import java.net.http.HttpRequest;
import java.time.Duration;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import net.jodah.failsafe.CircuitBreaker;
import net.jodah.failsafe.Failsafe;
import net.jodah.failsafe.FailsafeExecutor;
import net.jodah.failsafe.Fallback;
import net.jodah.failsafe.Timeout;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Embeddable
public class Email implements Serializable {

  @Column(unique = true)
  private String email;

  public Email(String email, Uniqueness uniqueness, Blacklist blacklist) {
    var isNotBlank = email != null && !email.isBlank();
    checkArgument(isNotBlank, "Email %s must not be blank", email);

    if (!uniqueness.guaranteed(email)) {
      throw new NotUniqueException(this);
    }

    if (!blacklist.allows(email)) {
      throw new BlacklistedException(this);
    }

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


  static class NotUniqueException extends DomainException {
    NotUniqueException(Email email) {
      super("Email " + email + " is not unique");
    }
  }

  static class BlacklistedException extends DomainException {
    BlacklistedException(Email email) {
      super("Email " + email + " is blacklisted");
    }
  }


  public interface Uniqueness {

    Uniqueness ALWAYS_GUARANTEED = email -> true;

    boolean guaranteed(String email);

    @Component
    class AcrossCustomers implements Uniqueness {

      private final Customers customers;

      private AcrossCustomers(Customers customers) {
        this.customers = customers;
      }

      @Override
      public boolean guaranteed(String email) {
        return customers.singleByEmail(email).isEmpty();
      }

    }

  }

  public interface Blacklist {

    Blacklist ALWAYS_ALLOWS = email -> true;

    boolean allows(String email);

    @Component
    class External implements Blacklist {

      private static final String ALLOW = "ALLOW";

      private final String url;
      private final int port;

      private final CircuitBreaker<String> breaker;
      private final Timeout<String> timeout;
      private final FailsafeExecutor<String> failsafe;

      public External(@Value("${blacklist.url}") String url, @Value("${blacklist.port}") int port) {
        this.url = url;
        this.port = port;

        this.timeout = Timeout.<String>of(ofSeconds(1)).withCancel(true);

        this.breaker = new CircuitBreaker<String>()
            .withFailureThreshold(4)
            .withDelay(ofSeconds(30));

        var fallback = Fallback.of(ALLOW);

        this.failsafe = Failsafe.with(fallback, breaker, timeout);
      }

      Duration timeout() {
        return timeout.getTimeout();
      }

      void halfOpenCircuit() {
        breaker.halfOpen();
      }

      @Override
      public boolean allows(String email) {
        return safelyAllows(email).equals(ALLOW);
      }

      private String safelyAllows(String email) {
        return failsafe.get(() -> unsafelyAllows(email));
      }

      private String unsafelyAllows(String email) throws Exception {
        var uri = new URI(url + ":" + port + "/" + email);
        var request = HttpRequest.newBuilder()
            .uri(uri)
            .build();
        return newHttpClient().send(request, ofString()).body();
      }

    }

  }
}
