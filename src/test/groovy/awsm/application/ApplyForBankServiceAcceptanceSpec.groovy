package awsm.application

import awsm.base.BaseAcceptanceSpec
import org.springframework.test.web.servlet.result.MockMvcResultHandlers

import static org.hamcrest.text.CharSequenceLength.hasLength
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class ApplyForBankServiceAcceptanceSpec extends BaseAcceptanceSpec {

    def validApplication() {
        [
                email: fake().internet().emailAddress(),
                firstName: fake().name().firstName(),
                lastName: fake().name().lastName(),
                countryOfResidence: fake().country().countryCode2(),
                dateOfBirth: '1988-10-10'
        ]
    }

    def "valid application"() {
        when: 'I apply for bank services'
            def _ = perform jsonPost("/applications", validApplication())
        then: 'I get my application id'
            _.andExpect status().isOk()
            _.andDo(MockMvcResultHandlers.print())
            _.andExpect content().string(hasLength(10))
    }


    def "cannot apply with blacklisted email"() {
        given: 'An email is in the blacklist'
            blacklist().disallow(domain)
        when: 'I apply with a blacklisted email'
            def _ = perform jsonPost("/applications", [
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

    def "cannot apply without first name, last name or email"() {
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

    def "cannot apply with the same email"() {
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
