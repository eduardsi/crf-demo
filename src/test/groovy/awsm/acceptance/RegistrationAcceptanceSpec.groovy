package awsm.acceptance

import org.springframework.test.web.servlet.ResultActions

import static org.hamcrest.Matchers.hasLength
import static org.hamcrest.Matchers.is
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status


class RegistrationAcceptanceSpec extends BaseAcceptanceSpec {

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
            ResultActions completeRegistration = register(registrationInfo())

        then: 'I should get back my encrypted personal id'
            completeRegistration.andExpect status().isOk()
            completeRegistration.andExpect jsonPath('$.personalId', hasLength(32))
    }

    def 'throws if email is not unique'() {
        def registrationInfo = registrationInfo()
        when: 'I complete registration'
            register(registrationInfo)
        and: 'I complete registration with the same data'
            ResultActions completeRegistration = register(registrationInfo)
        then:
            completeRegistration.andExpect status().isBadRequest()
            completeRegistration.andExpect jsonPath('$.[0]', is("email is taken"))
    }

    def 'throw if email, first name, last name, or personal id are missing'() {
        when: 'I complete registration with no data provided'
            ResultActions completeRegistration = register([
                    firstName: '',
                    lastName: '',
                    personalId: '',
                    email: ''
            ])
        then:
            completeRegistration.andExpect status().isBadRequest()
            completeRegistration.andExpect jsonPath('$.[0]', is("firstName is missing"))
            completeRegistration.andExpect jsonPath('$.[1]', is("lastName is missing"))
            completeRegistration.andExpect jsonPath('$.[2]', is("personalId is missing"))
            completeRegistration.andExpect jsonPath('$.[3]', is("email is missing"))
    }

    private ResultActions register(registrationForm) {
        mvc.perform jsonPost("/registrations", registrationForm)
    }

}
