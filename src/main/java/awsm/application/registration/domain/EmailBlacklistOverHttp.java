package awsm.application.registration.domain;

import static java.net.http.HttpClient.newHttpClient;
import static java.net.http.HttpResponse.BodyHandlers.ofString;
import static java.time.Duration.ofSeconds;

import java.net.URI;
import java.net.http.HttpRequest;
import java.time.Duration;
import net.jodah.failsafe.CircuitBreaker;
import net.jodah.failsafe.Failsafe;
import net.jodah.failsafe.FailsafeExecutor;
import net.jodah.failsafe.Fallback;
import net.jodah.failsafe.Timeout;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
class EmailBlacklistOverHttp implements EmailBlacklist {

  private static final String ALLOW = "ALLOW";

  private final String url;
  private final int port;

  private final CircuitBreaker<String> breaker;
  private final Timeout<String> timeout;
  private final FailsafeExecutor<String> failsafe;

  public EmailBlacklistOverHttp(@Value("${blacklist.url}") String url, @Value("${blacklist.port}") int port) {
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
