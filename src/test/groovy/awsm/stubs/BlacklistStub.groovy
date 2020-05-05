package awsm.stubs

import com.github.tomakehurst.wiremock.WireMockServer
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

import static com.github.tomakehurst.wiremock.client.WireMock.*

@Component
class BlacklistStub {

    private final WireMockServer blacklist

    BlacklistStub(@Value('${blacklist.port}') int blacklistPort) {
        this.blacklist = new WireMockServer(blacklistPort)
    }

    def start() {
        blacklist.start()
        blacklist.stubFor get(anyUrl())
                .willReturn(aResponse()
                        .withStatus(200)
                        .withBody("ALLOW"))
    }

    def disallow(String domain) {
        blacklist.stubFor get(urlPathMatching(".*($domain)"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withBody("BLOCK"))
    }

    def stop() {
        blacklist.stop()
    }

}

