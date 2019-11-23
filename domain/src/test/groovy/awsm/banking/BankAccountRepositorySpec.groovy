package awsm.banking

import awsm.base.BaseIntegrationSpec
import org.jooq.DSLContext
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.support.TransactionTemplate
import spock.lang.Ignore
import spock.lang.Subject

import javax.annotation.PostConstruct

import static Amount.amount

@Ignore
class BankAccountRepositorySpec extends BaseIntegrationSpec implements WithSampleBankAccount {

    @Autowired
    PlatformTransactionManager txManager

    @Autowired
    DSLContext dsl

    TransactionTemplate tx

    @Subject
    BankAccount.Repository repository

    @PostConstruct
    void init() {
        tx = new TransactionTemplate(txManager)
        repository = new BankAccount.Repository(dsl,
                new Transactions.Repository(dsl)
        )
    }

    def "supports saving and reading"() {
        given: "I perform deposit and withdrawal"
            account.deposit eur("50")
            account.withdraw eur("20")

        when: "I save my bank account in a repo"
            tx.executeWithoutResult { account.saveNew(repository) }

        and: "I read it back from the repo"
            def acc = tx.execute {
                repository.singleBy(account.id())
            }
            assert acc.balance() == amount("30.00")

        and: "I perform another deposit and withdrawal"
            acc.deposit eur("5")
            acc.withdraw eur("3")

        and: "I save my bank account"
            acc.save(repository)

        and: "I read it back"
            def acc2 = repository.singleBy(account.id())

        then: "I should see the correct balance"
            acc2.balance() == amount("32.00")
    }


}
