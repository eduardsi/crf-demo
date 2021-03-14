package awsm.api

import com.dumbster.smtp.SimpleSmtpServer
import com.github.javafaker.Faker
import groovy.json.JsonBuilder
import groovy.json.JsonSlurper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import spock.lang.AutoCleanup
import spock.lang.Shared
import spock.lang.Specification
import spock.util.concurrent.PollingConditions

import static org.hamcrest.Matchers.*
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc
class BankAccountControllerSpec extends Specification {

    @Autowired
    protected MockMvc mvc

    @Shared
    @AutoCleanup
    static def mockSmtpServer = SimpleSmtpServer.start(1025)

    def conditions = new PollingConditions(timeout: 5)

    static def application() {
        [
            firstName: fake().name().firstName(),
            lastName: fake().name().lastName(),
            personalId: fake().idNumber().valid(),
            email: fake().internet().emailAddress()
        ]
    }

    def 'new bank account'() {
        def application = application()

        when: 'I apply for a new bank account'

            def applyForAccount = mvc.perform post("/accounts")
                .content(new JsonBuilder(application).toPrettyString())
                .contentType(MediaType.APPLICATION_JSON)

        then: 'I am getting a bank account with a new iban'
            applyForAccount.andExpect status().isOk()
            applyForAccount.andExpect jsonPath("iban", startsWith('LV'))
            applyForAccount.andExpect jsonPath("iban", hasLength(21))

        then: 'I am receiving an opening bonus'
            applyForAccount.andExpect jsonPath("balance", is("5.00"))

        and: 'I am receiving a congratulations email'
        conditions.eventually {
            def email = mockSmtpServer.receivedEmails.first()
            assert email.getHeaderValue("To").contains(application.email)
            assert email.getHeaderValue("Subject").contains('Congratulations!')
            assert email.body == "Congratulations, $application.firstName $application.lastName. Thanks for using our services"
        }

    }

    def 'deposits and withdrawals'() {
        def application = application()
        given: 'I have a bank account'
            def applyForAccount = mvc.perform post("/accounts")
                .content(new JsonBuilder(application).toPrettyString())
                .contentType(MediaType.APPLICATION_JSON)
            def response = applyForAccount.andReturn().response
            def content = new JsonSlurper().parseText(response.contentAsString)

        and: 'I deposit one million'
            def deposit = mvc.perform post("/accounts/${content.iban}/deposits")
                .param("amount", "1000000")
            deposit.andExpect status().isOk()
        when: 'I withdraw that million'
            def withdrawal = mvc.perform post("/accounts/${content.iban}/withdrawals")
                .param("amount", "1000000")
        then: 'Withdrawal should succeed'
            withdrawal.andExpect status().isOk()
    }

    static Faker fake() {
        return new Faker()
    }
}
