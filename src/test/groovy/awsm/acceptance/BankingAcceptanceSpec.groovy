package awsm.acceptance


import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.web.servlet.MockMvc
import spock.util.concurrent.PollingConditions

import static org.hamcrest.Matchers.hasLength
import static org.hamcrest.Matchers.startsWith
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class BankingAcceptanceSpec extends BaseAcceptanceSpec {

    def conditions = new PollingConditions(timeout: 10)

    def validApplication() {
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
            def applyForAccount = mvc.perform jsonPost("/bank-accounts", application)

        then: 'I am getting a bank account with a new iban'
            applyForAccount.andExpect status().isOk()
            applyForAccount.andExpect jsonPath("iban", startsWith('LV'))
            applyForAccount.andExpect jsonPath("iban", hasLength(21))

        and: 'I am receiving a congratulations email'
        conditions.eventually {
            def outgoingEmail = outgoingEmails().elementWithIndex(0)
            assert outgoingEmail.field("Content", "Headers", "To").contains(application.email)
            assert outgoingEmail.field("Content", "Headers", "Subject").contains('Congratulations!')
            assert outgoingEmail.field("Content", "Body").isEqualTo("Congratulations, $application.firstName $application.lastName. Thanks for using our services")
        }

    }

}
