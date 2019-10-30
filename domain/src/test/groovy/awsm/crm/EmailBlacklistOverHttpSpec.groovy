package awsm.crm

import com.github.javafaker.Faker
import com.github.tomakehurst.wiremock.WireMockServer
import spock.lang.Specification
import spock.util.concurrent.PollingConditions

import static com.github.tomakehurst.wiremock.client.WireMock.*
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig
import static com.github.tomakehurst.wiremock.matching.RequestPatternBuilder.allRequests

class EmailBlacklistOverHttpSpec extends Specification {

    def "short circuits after four timeouts"() {
        given: "Blacklist points to a slow server"
            def wireMock = new WireMockServer(wireMockConfig().dynamicPort().dynamicPort());
            wireMock
                    .stubFor(get(anyUrl())
                            .willReturn(
                                    aResponse()
                                            .withStatus(200)
                                            .withFixedDelay(5000)))
            wireMock.start()

        def blacklist = new EmailBlacklistOverHttp("http://localhost", wireMock.port());

        when: "Someone performs a series of blacklist checks"
            performNumberOfEmailChecks(blacklist, 10)

        then: "Circuit must open after 4 exceptions"
            wireMock.verify(4, allRequests())

        when: "Circuit half-opens after a while"
            blacklist.halfOpenCircuit()

        and: "Someone performs a series of blacklist checks"
            performNumberOfEmailChecks(blacklist, 10)

        then: "Circuit must open after 4 exceptions, because the server is still slow"
        wireMock.verify(8, allRequests())

        when: "We fix the server"
        wireMock
                .stubFor(get(anyUrl())
                        .willReturn(
                                aResponse()
                                        .withStatus(200)));

        and: "Circuit half-opens after a while"
            blacklist.halfOpenCircuit()

        and: "Someone performs a series of blacklist checks"
            performNumberOfEmailChecks(blacklist, 10)

        then: "All checks hit the server"
            wireMock.verify(18, allRequests())
    }

    def performNumberOfEmailChecks(EmailBlacklist blacklist, int count) {
        count.times {
            blacklist.allows(someEmail())
        }
    }

    def "allows all emails if a server timeouts"() {
        when: "Blacklist points to a dead server"
            def blacklist = new EmailBlacklistOverHttp("http://dead.server", 9999)
        then: "Blacklist doesn't wait forever and allows all emails"
            new PollingConditions(timeout: 1.5).eventually {
                assert blacklist.allows(someEmail())
            }
    }

    private static Email someEmail() {
        new Email(new Faker().internet().emailAddress())
    }

}
