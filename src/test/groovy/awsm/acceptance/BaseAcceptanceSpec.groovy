package awsm.acceptance

import com.toomuchcoding.jsonassert.JsonPath
import com.toomuchcoding.jsonassert.JsonVerifiable
import groovy.json.JsonSlurper
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.HttpClientBuilder
import org.apache.http.util.EntityUtils
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.util.TestPropertyValues
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.test.context.ContextConfiguration
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.wait.strategy.Wait
import org.testcontainers.spock.Testcontainers
import org.testcontainers.utility.DockerImageName
import spock.lang.Shared
import spock.lang.Specification

import static org.apache.http.util.EntityUtils.toString

@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration(initializers = Initializer)
abstract class BaseAcceptanceSpec extends Specification implements WithFaker, WithJsonOperations {

    private static final MAILHOG_PORT_SMTP = 1025
    private static final MAILHOG_PORT_HTTP = 8025

    private static GenericContainer MAILHOG_SINGLETON = new GenericContainer(new DockerImageName("mailhog/mailhog:v1.0.1"))
            .withExposedPorts(MAILHOG_PORT_SMTP, MAILHOG_PORT_HTTP)
            .waitingFor(Wait.forHttp("/"))

    @Shared
    GenericContainer mailhog = MAILHOG_SINGLETON

    static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        @Override
        void initialize(ConfigurableApplicationContext applicationContext) {
            TestPropertyValues.of(
                    "simplejavamail.smtp.host=${MAILHOG_SINGLETON.getContainerIpAddress()}",
                    "simplejavamail.smtp.port=${MAILHOG_SINGLETON.getMappedPort(MAILHOG_PORT_SMTP)}"
            ).applyTo(applicationContext.environment)
        }
    }

    JsonVerifiable outgoingEmails() {
        def httpClient = HttpClientBuilder.create().build()
        def httpGet = new HttpGet("http://${mailhog.containerIpAddress}:${mailhog.getMappedPort(MAILHOG_PORT_HTTP)}/api/v2/messages")
        def httpResponse = httpClient.execute(httpGet)

        return JsonPath.builder(toString(httpResponse.entity)).array("items")
    }

}
