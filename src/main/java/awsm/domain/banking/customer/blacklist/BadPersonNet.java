package awsm.domain.banking.customer.blacklist;

import static java.net.http.HttpClient.newHttpClient;
import static java.net.http.HttpResponse.BodyHandlers.ofString;
import static java.time.Duration.ofSeconds;

import java.net.URI;
import java.net.http.HttpRequest;
import java.time.Duration;

import awsm.domain.banking.customer.Email;
import net.jodah.failsafe.CircuitBreaker;
import net.jodah.failsafe.Failsafe;
import net.jodah.failsafe.FailsafeExecutor;
import net.jodah.failsafe.Fallback;
import net.jodah.failsafe.Timeout;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
class BadPersonNet implements Blacklist {

  private static final String ALLOW = "ALLOW";

  private final String url;
  private final int port;

  private final CircuitBreaker<String> breaker;
  private final Timeout<String> timeout;
  private final FailsafeExecutor<String> failsafe;

  public BadPersonNet(@Value("${blacklist.url}") String url, @Value("${blacklist.port}") int port) {
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
  public boolean permits(Email email) {
    return safelyAllows(email).equals(ALLOW);
  }

  private String safelyAllows(Email email) {
    return failsafe.get(() -> unsafelyAllows(email));
  }

  private String unsafelyAllows(Email email) throws Exception {
    var uri = new URI(url + ":" + port + "/" + email);
    var request = HttpRequest.newBuilder()
        .uri(uri)
        .build();
    return newHttpClient().send(request, ofString()).body();
  }

}
