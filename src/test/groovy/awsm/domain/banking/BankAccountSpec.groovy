package awsm.domain.banking

import awsm.domain.AllDomainEvents
import awsm.domain.core.Amount
import awsm.infrastructure.clock.TimeMachine
import org.skyscreamer.jsonassert.JSONAssert
import org.skyscreamer.jsonassert.JSONCompareMode
import org.springframework.mock.env.MockEnvironment
import spock.lang.Specification
import spock.lang.Subject
import spock.util.time.MutableClock

import static awsm.infrastructure.clock.TimeMachine.today
import static java.time.Duration.ofDays
import static java.time.Instant.EPOCH
import static java.time.ZoneOffset.UTC

class BankAccountSpec extends Specification {

    def clock = new MutableClock(EPOCH, UTC)

    def events = new AllDomainEvents()

    def accountHolder = new AccountHolder("Eduards", "Sizovs", "210888-12345", "eduards@sizovs.net")

    def defaultLimits = WithdrawalLimits.defaults(new MockEnvironment()
            .withProperty("banking.account-limits.daily", "100.00")
            .withProperty("banking.account-limits.monthly", "1000.00"))

    @Subject
    @Delegate
    BankAccount account = new BankAccount(accountHolder, defaultLimits)

    def setup() {
        set(events)
        TimeMachine.set(clock)
    }

    def "can be closed"() {
        given: "Account is open"
        open()
        assert isOpen()

        when: "I try to close the account"
        close(UnsatisfiedObligations.NONE)

        then: "Account must close"
        isClosed()
    }

    def "cannot be closed if some unsatisfied obligations exist"() {
        given: "I have some unsatisfied obligations"
        def unsatisfiedObligations = { true } as UnsatisfiedObligations

        when: "I close my account"
        close(unsatisfiedObligations)

        then: "I get an error"
        def e = thrown(IllegalStateException)
        e.message == "Bank account cannot be closed because a holder has unsatisfied obligations"
    }

    def "supports deposits"() {
        given: "account is open"
        open()

        and: "I am out of cash"
        assert balance() == Amount.ZERO

        when: "I deposit some cash"
        def tx = deposit(Amount.of(100.00))

        then: "A deposit transaction should be created"
        tx.isDeposit()
        tx.deposited() == Amount.of(100.00)
        tx.withdrawn() == Amount.of(0.00)
    }

    def "supports withdrawals"() {
        given: "account is open"
        open()

        and: "I have some cash"
        deposit(Amount.of(100.00))
        assert balance() == Amount.of(100.00)

        when: "I withdraw it"
        def tx = withdraw(Amount.of(100.00))

        then: "A withdrawal transaction should be created"
        tx.isWithdrawal()
        tx.withdrawn() == Amount.of(100.00)
        tx.deposited() == Amount.of(0.00)
        events.any { it -> new WithdrawalHappened(iban(), tx.uid()) }
    }

    def "cannot be withdrawn when closed"() {
        given: "Account is closed"
        close(UnsatisfiedObligations.NONE)

        when: "I try to withdraw my cash"
        withdraw(Amount.of(100.00))

        then: "I get an error"
        def e = thrown(IllegalStateException)
        e.message == "Account is not open."
    }

    def "cannot be deposited when closed"() {
        given: "Account is closed"
        close(UnsatisfiedObligations.NONE)

        when: "I try to deposit some cash"
        deposit(Amount.of(100.00))

        then: "I get an error"
        def e = thrown(IllegalStateException)
        e.message == "Account is not open."
    }

    def "cannot be withdrawn for the amount that exceeds the balance"() {
        given: "Account is open"
        open()

        and: "I am out of money"
        assert balance() == Amount.ZERO

        when: "I withdraw some cash"
        withdraw(Amount.of(1.00))

        then: "I get an error"
        def e = thrown(IllegalStateException)
        e.message == "Not enough funds available on your account."
    }

    def "cannot be withdrawn for the amount that exceeds the daily limit"() {
        given: "Account is open"
        open()

        and: "I have some spare cash"
        deposit(Amount.of(1000.00))

        when: "I withdraw more than allowed by daily limit"
        withdraw(Amount.of(101.00))

        then: "I get an error"
        def e = thrown(IllegalStateException)
        e.message == "Daily withdrawal limit (100.00) reached."
    }

    def "cannot be withdrawn for the amount than exceeds the monthly limit"() {
        given: "Account is open"
        open()

        and: "I have some spare cash"
        deposit(Amount.of(2000.00))

        when: "I withdraw more than allowed by monthly limit"
        withdraw(Amount.of(1001.00))

        then: "I get an error"
        def e = thrown(IllegalStateException)
        e.message == "Monthly withdrawal limit (1000.00) reached."
    }

    def "cannot be closed if unsatisfied obligations exist"() {
        given: "Account is open"
        open()

        and: "I have some unsatisfied obligations"
        def unsatisfiedObligations = { true } as UnsatisfiedObligations

        when: "I close my account"
        close unsatisfiedObligations

        then: "I get an error"
        def e = thrown(IllegalStateException)
        e.message == "Bank account cannot be closed because a holder has unsatisfied obligations"
    }


    def "publishes a BankAccountOpened event"() {
        when: "I try to open a bank account"
        open()

        then: "An event gets published"
        events.any { it -> new BankAccountOpened(iban())}
    }

    def "calculates a balance"() {
        given: "Account is open"
        open()

        when: "I try to deposit and withdraw some cash"
        deposit(Amount.of(100.00))
        withdraw(Amount.of(20.50))
        withdraw(Amount.of(20.00))
        def balance = balance()

        then: "My balance shows a sum of all transactions"
        balance == Amount.of(59.50)
    }

    def "provides statement for a given time interval"() {
        given: "Account is open"
        open()

        and: "I perform a series of deposits and withdrawals on different days"

        clock + ofDays(1)
        deposit(Amount.of(100.00))

        clock + ofDays(1)
        def from = today()
        deposit(Amount.of(99.00))

        clock + ofDays(1)
        def to = today()
        withdraw(Amount.of(98.00))

        clock + ofDays(1)
        withdraw(Amount.of(2.00))

        when: "I ask for a bank statement"
        def actual = statement(from, to).json()

        then: "I should see all operations as a nicely formatted JSON"
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
                    "transactions": [
                      {
                        "time": "1970-01-03T00:00:00",
                        "deposit": "99.00",
                        "withdrawal": "0.00",
                        "balance": "199.00"
                      },
                      {
                        "time": "1970-01-04T00:00:00",
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
