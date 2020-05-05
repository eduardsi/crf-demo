package awsm.domain.crm

import awsm.domain.banking.customer.Email
import spock.lang.Specification

class EmailSpec extends Specification {

    def "cannot be empty"() {
        when:
            new Email("  ")
        then:
            thrown(IllegalArgumentException)
    }

    def "can be turned into string"() {
        when:
            def email = new Email("whatever@email.com")
        then:
            email.toString() == "whatever@email.com"
    }

}
