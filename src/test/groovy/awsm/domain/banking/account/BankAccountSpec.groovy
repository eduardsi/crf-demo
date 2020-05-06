package awsm.domain.banking.account

import awsm.domain.banking.UnsatisfiedObligations
import org.junit.Before
import org.skyscreamer.jsonassert.JSONAssert
import org.skyscreamer.jsonassert.JSONCompareMode
import org.threeten.extra.Days
import org.threeten.extra.MutableClock
import spock.lang.Specification

import static awsm.domain.banking.commons.Amount.amount
import static awsm.infrastructure.time.TimeMachine.today
import static awsm.infrastructure.time.TimeMachine.with

class BankAccountSpec extends Specification implements WithSampleBankAccount {

    private def clock = MutableClock.epochUTC()

    @Before
    void beforeEach() {
        with(clock)
    }

    def "supports deposits"() {
        given: "I am out of cash"
            assert account.balance() == amount("0.00")
        when: "I deposit some cash"
            def tx = account.deposit amount("100.00")
        then: "A deposit transaction should be created"
            tx.isDeposit()
            tx.deposited() == amount("100.00")
            tx.withdrawn() == amount("0.00")
    }

    def "supports withdrawals"() {
        given: "I have some cash"
            account.deposit amount("100.00")
            assert account.balance() == amount("100.00")
        when: "I withdraw it"
            def tx = account.withdraw amount("100.00")
        then: "A withdrawal transaction should be created"
            tx.isWithdrawal()
            tx.withdrawn() == amount("100.00")
            tx.deposited() == amount("0.00")
    }

    def "cannot withdraw if closed"() {
        given: "I have some spare cash"
            account.deposit amount("100.00")
        and: "Bank account is closed"
            account.close UnsatisfiedObligations.NONE
        when: "I withdraw my cash"
            account.withdraw amount("100.00")
        then: "I get an error"
            def e = thrown(IllegalStateException)
            e.message == "Account is closed."
    }

    def "cannot deposit if closed"() {
        given: "Bank account is closed"
            account.close UnsatisfiedObligations.NONE
        when: "I deposit some cash"
            account.deposit amount("100.00")
        then: "I get an error"
            def e = thrown(IllegalStateException)
            e.message == "Account is closed."
    }

    def "cannot withdraw more funds than available"() {
        given: "I am out of money"
            assert account.balance() == amount("0.00")
        when: "I withdraw some cash"
            account.withdraw amount("1.00")
        then: "I get an error"
            def e = thrown(IllegalStateException)
            e.message == "Not enough funds available on your account."
    }

    def "cannot withdraw more funds than allowed by daily limit"() {
        given: "I have some spare cash"
            account.deposit amount("1000.00")
        when: "I withdraw more than allowed by daily limit"
            account.withdraw amount("101.00")
        then: "I get an error"
            def e = thrown(IllegalStateException)
            e.message == "Daily withdrawal limit (100.00) reached."
    }

    def "cannot withdraw more funds than allowed by monthly limit"() {
        given: "I have some spare cash"
            account.deposit amount("2000.00")
        when: "I withdraw more than allowed by monthly limit"
            account.withdraw amount("1001.00")
        then: "I get an error"
            def e = thrown(IllegalStateException)
            e.message == "Monthly withdrawal limit (1000.00) reached."
    }

    def "cannot close if some unsatisfied obligations exist"() {
        given: "I have some unsatisfied obligations"
            def unsatisfiedObligations = { true } as UnsatisfiedObligations
        when: "I close my account"
            account.close unsatisfiedObligations
        then: "I get an error"
            def e = thrown(IllegalStateException)
            e.message == "Bank account cannot be closed because a holder has unsatified obligations"
    }

    def "provides statement for a given time interval"() {
        given: "I perform a series of deposits and withdrawals on different days"
            clock.add Days.ONE
            account.deposit amount("100.00")

            clock.add Days.ONE
            def from = today()
            account.deposit amount("99.00")

            clock.add Days.ONE
            def to = today()
            account.withdraw amount("98.00")

            clock.add Days.ONE
            account.withdraw amount("2.00")

        when: "I ask for a bank statement"
            def actual = account.statement(from, to).json()

        then: "I should see all operations in a nicely formatted Json"
            def expected = """
                  {
                    "startingBalance": {
                      "date": "1970-01-03",
                      "amount": "100.00"
                    },
                    "closingBalance": {
                      "date": "1970-01-04",
                      "amount": "101.00"
                    },
                    "transactions":[
                      {
                        "awsm.time": "1970-01-03T00:00:00",
                        "deposit": "99.00",
                        "withdrawal": "0.00",
                        "balance": "199.00"
                      },
                      {
                        "awsm.time": "1970-01-04T00:00:00",
                        "deposit": "0.00",
                        "withdrawal" :"98.00",
                        "balance": "101.00"
                      }
                    ]
                   }
                """

        JSONAssert.assertEquals(expected, actual, JSONCompareMode.STRICT)
    }


}
