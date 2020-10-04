package awsm.banking


import awsm.infrastructure.clock.TimeMachine
import org.junit.Before
import org.springframework.mock.env.MockEnvironment
import org.threeten.extra.MutableClock
import spock.lang.Specification

class BankAccountSpec extends Specification {

    def clock = MutableClock.epochUTC()

    def events = Mock(DomainEvents)

    def accountHolder = new AccountHolder("Eduards", "Sizovs")

    def defaultLimits = WithdrawalLimits.defaults(new MockEnvironment()
            .withProperty("banking.account-limits.daily", "100.00")
            .withProperty("banking.account-limits.monthly", "1000.00"))

    def account = new BankAccount(accountHolder, defaultLimits)

    @Before
    void beforeEach() {
        account.events = events
        TimeMachine.with(clock)
    }

    def "can be closed"() {
        given: "Account is open"
        account.open()
        assert account.isOpen()

        when: "I try to close the account"
        account.close(UnsatisfiedObligations.NONE)

        then: "Account must close"
        account.isClosed()
    }

    def "cannot be closed if some unsatisfied obligations exist"() {
        given: "I have some unsatisfied obligations"
        def unsatisfiedObligations = { true } as UnsatisfiedObligations

        when: "I close my account"
        account.close(unsatisfiedObligations)

        then: "I get an error"
        def e = thrown(IllegalStateException)
        e.message == "Bank account cannot be closed because a holder has unsatisfied obligations"
    }

    def "supports deposits"() {
        given: "account is open"
        account.open()

        and: "I am out of cash"
        assert account.balance() == 0.00

        when: "I deposit some cash"
        def tx = account.deposit 100.00

        then: "A deposit transaction should be created"
        tx.isDeposit()
        tx.deposited() == 100.00
        tx.withdrawn() == 0.00
    }

    def "supports withdrawals"() {
        given: "account is open"
        account.open()

        and: "I have some cash"
        account.deposit(100.00)
        assert account.balance() == 100.00

        when: "I withdraw it"
        def tx = account.withdraw(100.00)

        then: "A withdrawal transaction should be created"
        tx.isWithdrawal()
        tx.withdrawn() == 100.00
        tx.deposited() == 0.00
    }

    def "cannot be withdrawn if closed"() {
        given: "Account is closed"
        account.close(UnsatisfiedObligations.NONE)

        when: "I try to withdraw my cash"
        account.withdraw(100.00)

        then: "I get an error"
        def e = thrown(IllegalStateException)
        e.message == "Account is not open."
    }

    def "cannot be deposited when closed"() {
        given: "Account is closed"
        account.close(UnsatisfiedObligations.NONE)

        when: "I try to deposit some cash"
        account.deposit(100.00)

        then: "I get an error"
        def e = thrown(IllegalStateException)
        e.message == "Account is not open."
    }

    def "cannot be withdrawn for the amount that exceeds the balance"() {
        given: "Account is open"
        account.open()

        and: "I am out of money"
        assert account.balance() == 0.00

        when: "I withdraw some cash"
        account.withdraw(1.00)

        then: "I get an error"
        def e = thrown(IllegalStateException)
        e.message == "Not enough funds available on your account."
    }

    def "cannot be withdrawn for the amount that exceeds the daily limit"() {
        given: "Account is open"
        account.open()

        and: "I have some spare cash"
        account.deposit(1000.00)

        when: "I withdraw more than allowed by daily limit"
        account.withdraw(101.00)

        then: "I get an error"
        def e = thrown(IllegalStateException)
        e.message == "Daily withdrawal limit (100.00) reached."
    }

    def "cannot be withdrawn for the amount than exceeds the monthly limit"() {
        given: "Account is open"
        account.open()

        and: "I have some spare cash"
        account.deposit(2000.00)

        when: "I withdraw more than allowed by monthly limit"
        account.withdraw(1001.00)

        then: "I get an error"
        def e = thrown(IllegalStateException)
        e.message == "Monthly withdrawal limit (1000.00) reached."
    }

    def "cannot be closed if unsatisfied obligations exist"() {
        given: "Account is open"
        account.open()

        and: "I have some unsatisfied obligations"
        def unsatisfiedObligations = { true } as UnsatisfiedObligations

        when: "I close my account"
        account.close unsatisfiedObligations

        then: "I get an error"
        def e = thrown(IllegalStateException)
        e.message == "Bank account cannot be closed because a holder has unsatisfied obligations"
    }


    def "publishes a BankAccountOpened event"() {
        when: "I try to open a bank account"
        account.open()

        then: "An event gets published"
        1 * events.publish(new BankAccountOpened(account.iban()))
    }

    def "calculates a balance"() {
        given: "Account is open"
        account.open()

        when: "I try to deposit and withdraw some cash"
        account.deposit(100.00)
        account.withdraw(20.50)
        account.withdraw(20.00)
        def balance = account.balance()

        then: "My balance shows a sum of all transactions"
        balance == 59.50
    }

}
