package awsm.application.registration.domain;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.anyUrl;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static com.github.tomakehurst.wiremock.matching.RequestPatternBuilder.allRequests;
import static java.time.Duration.ofSeconds;
import static org.awaitility.Awaitility.await;

import com.github.javafaker.Faker;
import com.github.tomakehurst.wiremock.WireMockServer;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("email blacklist over http")
class EmailBlacklistOverOverHttpTest {

  @Test
  void timeouts_and_allows_all_emails_if_host_is_unreachable() {
    var errorThreshold = ofSeconds(1);
    var blacklist = new EmailBlacklistOverHttp("http://unreachable.host", 9999);
    await()
        .atMost(blacklist.timeout().plus(errorThreshold))
        .until(() -> blacklist.allows(anyEmail()));
  }

  @Test
  void short_circuits_after_four_timeouts() {
    var wireMock = new WireMockServer(wireMockConfig().dynamicPort().dynamicPort());

    // make the server slow
    wireMock
        .stubFor(get(anyUrl())
            .willReturn(
                aResponse()
                    .withStatus(200)
                    .withFixedDelay(5000)));
    wireMock.start();

    var blacklist = new EmailBlacklistOverHttp("http://localhost", wireMock.port());

    // circuit must open after 4 exceptions
    performNumberOfEmailChecks(blacklist, 10);
    wireMock.verify(4, allRequests());

    // ok, let's check again
    blacklist.halfOpenCircuit();

    // still failing
    // circuit must open after 4 exceptions
    performNumberOfEmailChecks(blacklist, 10);
    wireMock.verify(8, allRequests());

    // fix the server
    wireMock
        .stubFor(get(anyUrl())
            .willReturn(
                aResponse()
                    .withStatus(200)));

    // check after the fix
    blacklist.halfOpenCircuit();

    // not failing anymore
    performNumberOfEmailChecks(blacklist, 10);
    wireMock.verify(18, allRequests());

  }

  private void performNumberOfEmailChecks(EmailBlacklist blacklist, int count) {
    Stream.generate(() -> anyEmail()).limit(count).forEach(email -> blacklist.allows(email));
  }

  private static Email anyEmail() {
    return new Email(
        new Faker().internet().emailAddress()
    );
  }


}