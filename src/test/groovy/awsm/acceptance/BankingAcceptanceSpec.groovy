package awsm.acceptance

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc

import static org.hamcrest.Matchers.hasLength
import static org.hamcrest.Matchers.startsWith
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.result.MockMvcResultHandlers


@SpringBootTest
@AutoConfigureMockMvc
class BankingAcceptanceSpec extends BaseAcceptanceSpec {

    @Autowired
    MockMvc mvc

    def validApplication() {
        [
                firstName: fake().name().firstName(),
                lastName: fake().name().lastName(),
                personalId: fake().idNumber().valid()
        ]
    }

    def 'for every newly opened bank account, account holders gets 10$ bonus'() {
        when: 'I apply for bank services'
        def _ = mvc.perform jsonPost("/bank-accounts", validApplication())

        then: 'I get my application id'
        _.andExpect status().isOk()
        _.andDo(MockMvcResultHandlers.print())
        _.andExpect jsonPath("iban", startsWith('LV'))
        _.andExpect jsonPath("iban", hasLength(21))
    }

}
