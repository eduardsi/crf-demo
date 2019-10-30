package awsm.banking

import spock.lang.Specification

import static Amount.amount

class WithdrawalLimitsSpec extends Specification {

    def "ensures that monthly limit is always higher than daily limit"() {
        when:
            new WithdrawalLimits(amount(daily), amount(monthly))
        then:
            def e = thrown(IllegalStateException)
            e.message == exception
        where:
            daily    || monthly  || exception
            "101.00" || "100.00" || "Monthly limit (100.00) must be higher than daily limit (101.00)"
            "100.00" || "100.00" || "Monthly limit (100.00) must be higher than daily limit (100.00)"
    }

}
