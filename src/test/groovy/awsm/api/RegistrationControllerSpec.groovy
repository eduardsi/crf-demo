package awsm.api

import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.result.MockMvcResultHandlers

import static org.hamcrest.Matchers.hasLength
import static org.hamcrest.Matchers.is
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status


class RegistrationControllerSpec extends BaseAcceptanceSpec {

    def registrationInfo() {
        [
                firstName: fake().name().firstName(),
                lastName: fake().name().lastName(),
                personalId: fake().idNumber().valid(),
                email: fake().internet().emailAddress()
        ]
    }

    def 'customer registration'() {
        when: 'I complete registration'
            def completeRegistration = register(registrationInfo())

        then: 'I should get back my encrypted personal id'
            completeRegistration.andExpect status().isOk()
            completeRegistration.andExpect jsonPath('$.personalId', hasLength(32))
    }

    def 'throws if email is not unique'() {
        def registrationInfo = registrationInfo()
        when: 'I complete registration'
            register(registrationInfo)
        and: 'I complete registration with the same data'
            def completeRegistration = register(registrationInfo)
        then:
            completeRegistration.andExpect status().isBadRequest()
            completeRegistration.andExpect jsonPath('$.email', is("must be unique"))
    }

    def 'throw if email, first name, last name, or personal id are missing'() {
        when: 'I complete registration with no data provided'
            def completeRegistration = register([
                    firstName: '',
                    lastName: '',
                    personalId: '',
                    email: ''
            ])
        then:
            completeRegistration.andExpect status().isBadRequest()
            completeRegistration.andDo(MockMvcResultHandlers.print())
            completeRegistration.andExpect jsonPath('$.firstName', is("must not be empty"))
            completeRegistration.andExpect jsonPath('$.lastName', is("must not be empty"))
            completeRegistration.andExpect jsonPath('$.personalId', is("must not be empty"))
            completeRegistration.andExpect jsonPath('$.email', is("must not be empty"))
    }

    private ResultActions register(registrationForm) {
        mvc.perform jsonPost("/registrations", registrationForm)
    }

}
