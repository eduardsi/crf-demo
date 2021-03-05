package awsm.acceptance

import com.icegreen.greenmail.spring.GreenMailBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.web.servlet.MockMvc
import spock.lang.Specification

import javax.mail.internet.MimeMessage

@SpringBootTest
@AutoConfigureMockMvc
abstract class BaseAcceptanceSpec extends Specification implements WithFaker, WithJsonOperations {

    @Autowired
    private GreenMailBean greenMailBean


//    GreenMail greenMail = new GreenMail(ServerSetupTest.SMTP_IMAP)
//    GreenMailUser greenMailUser = greenMail.setUser(
//            "someuser@somewhere.com", "someUser", "somePassword")

    private static final MAILHOG_PORT_SMTP = 1025
    private static final MAILHOG_PORT_HTTP = 8025

    @Autowired
    protected MockMvc mvc

    MimeMessage[] outgoingEmails() {
        greenMailBean.getReceivedMessages()

    }

    @DynamicPropertySource
    static void mailHogProperties(DynamicPropertyRegistry registry) {
        registry.add("simplejavamail.smtp.host", () -> 'localhost')
        registry.add("simplejavamail.smtp.port", () -> 1025)
    }

}
