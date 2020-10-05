package awsm.acceptance

import static org.hamcrest.Matchers.hasLength
import static org.hamcrest.Matchers.startsWith
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.result.MockMvcResultHandlers


class BankingAcceptanceSpec extends BaseAcceptanceSpecification {

    def validApplication() {
        [
                firstName: fake().name().firstName(),
                lastName: fake().name().lastName(),
                personalId: fake().idNumber().valid()
        ]
    }

    def 'for every newly opened bank account, account holders gets 10$ bonus on his account'() {
        when: 'I apply for bank services'
        def _ = perform jsonPost("/bank-accounts", validApplication())

        then: 'I get my application id'
        _.andExpect status().isOk()
        _.andDo(MockMvcResultHandlers.print())
        _.andExpect jsonPath("iban", startsWith('LV'))
        _.andExpect jsonPath("iban", hasLength(21))
    }

}
