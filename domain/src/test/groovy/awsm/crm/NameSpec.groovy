package awsm.crm

import spock.lang.Specification

class NameSpec extends Specification {

    def "can be turned into string"() {
        when:
            def name = new FullName("Eduards", "Sizovs")
        then:
            name.toString() == "Eduards Sizovs"
    }

}
