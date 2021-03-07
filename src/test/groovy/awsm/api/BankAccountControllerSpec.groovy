package awsm.api

import com.dumbster.smtp.SimpleSmtpServer
import com.github.javafaker.Faker
import groovy.json.JsonBuilder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import spock.util.concurrent.PollingConditions

import static org.hamcrest.Matchers.hasLength
import static org.hamcrest.Matchers.startsWith
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post

@SpringBootTest
@AutoConfigureMockMvc
class BankAccountControllerSpec {

    @Autowired
    protected MockMvc mvc

    private mockSmtpServer = SimpleSmtpServer.start(1025)

    def conditions = new PollingConditions(timeout: 5)

    static def validApplication() {
        [
            firstName: fake().name().firstName(),
            lastName: fake().name().lastName(),
            personalId: fake().idNumber().valid(),
            email: fake().internet().emailAddress()
        ]
    }

    def 'new bank account'() {
        def application = validApplication()

        when: 'I apply for a new bank account'

            def applyForAccount = mvc.perform post("/bank-accounts")
                .content(new JsonBuilder(application).toPrettyString())
                .contentType(MediaType.APPLICATION_JSON)

        then: 'I am getting a bank account with a new iban'
            applyForAccount.andExpect status().isOk()
            applyForAccount.andExpect jsonPath("iban", startsWith('LV'))
            applyForAccount.andExpect jsonPath("iban", hasLength(21))

        and: 'I am receiving a congratulations email'
        conditions.eventually {
            def email = mockSmtpServer.getReceivedEmails().first()
            assert email.getHeaderValue("To").contains(application.email)
            assert email.getHeaderValue("Subject").contains('Congratulations!')
            assert email.body == "Congratulations, $application.firstName $application.lastName. Thanks for using our services"
        }

    }

    static Faker fake() {
        return new Faker()
    }
}
