package awsm.application

import awsm.base.BaseAcceptanceSpec

import static org.hamcrest.text.CharSequenceLength.hasLength
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class ApplyForBankServicesAcceptanceSpec extends BaseAcceptanceSpec {

    def validRegistrationInfo() {
        [
                email: fake().internet().emailAddress(),
                firstName: fake().name().firstName(),
                lastName: fake().name().lastName()
        ]
    }

    def "registration"() {
        when: 'I register with valid registration info'
            def _ = perform jsonPost("/customers", validRegistrationInfo())
        then: 'I get my unique hashed customer id'
            _.andExpect status().isOk()
            _.andExpect jsonPath('$.customerHashId', hasLength(10))
    }


    def "cannot register with blacklisted email"() {
        given: 'An email is in the blacklist'
            blacklist().disallow(domain)
        when: 'I register with a blacklisted email'
            def _ = perform jsonPost("/customers", [
                    email: "$username@$domain",
                    firstName: fake().name().firstName(),
                    lastName: fake().name().lastName()
            ])
        then: 'I get an error'
            _.andExpect status().isUnprocessableEntity()
            _.andExpect jsonPath('$.[0]').value("email $username@$domain is blacklisted".toString())

        where:
        username                 || domain
        fake().name().username() || "pornhub.com"
        fake().name().username() || "rotten.com"
    }

    def "cannot register without first name, last name or email"() {
        when: 'I register without first name, last name and email'
            def _ = perform jsonPost("/customers", [
                    email: "",
                    firstName: "",
                    lastName: ""
            ])
        then: 'I get an error'
            _.andExpect status().isUnprocessableEntity()
            _.andExpect jsonPath('$.[0]').value('firstName is missing')
            _.andExpect jsonPath('$.[1]').value('lastName is missing')
            _.andExpect jsonPath('$.[2]').value('email is missing')
    }

    def "cannot register with the same email"() {
        given: 'Somebody has registered with an email'
            def email = fake().internet().emailAddress()
        perform jsonPost("/customers", [
                    email: email,
                    firstName: fake().name().firstName(),
                    lastName: fake().name().lastName()
            ])
        when: 'I register with the same email'
            def _ = perform jsonPost("/customers", [
                    email: email,
                    firstName: fake().name().firstName(),
                    lastName: fake().name().lastName()
            ])
        then: 'I get an error'
            _.andExpect status().isUnprocessableEntity()
            _.andExpect jsonPath('$.[0]').value("email is taken")
    }

}
