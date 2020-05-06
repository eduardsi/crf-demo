package awsm.domain.banking.account

import org.jooq.DSLContext
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.support.TransactionTemplate
import spock.lang.Specification
import spock.lang.Subject

import javax.annotation.PostConstruct

import static awsm.domain.banking.commons.Amount.amount

@SpringBootTest
class BankAccountRepoSpec extends Specification implements WithSampleBankAccount {

    @Autowired
    PlatformTransactionManager txManager

    @Autowired
    DSLContext dsl

    TransactionTemplate tx

    @Subject
    BankAccount.Repo repository

    @PostConstruct
    void init() {
        tx = new TransactionTemplate(txManager)
        repository = new BankAccount.Repo(dsl)
    }

    def "supports saving and reading"() {
        given: "I perform deposit and withdrawal"
            account.deposit amount("50")
            account.withdraw amount("20")

        when: "I save my bank account in a repo"
            tx.executeWithoutResult { account.open(dsl) }

        and: "I read it back from the repo"
            def acc = tx.execute {
                repository.singleBy(account.id())
            }
            assert acc.balance() == amount("30.00")

        and: "I perform another deposit and withdrawal"
            acc.deposit amount("5")
            acc.withdraw amount("3")

        and: "I save my bank account"
            acc.update(repository)

        and: "I read it back"
            def acc2 = repository.singleBy(account.id())

        then: "I should see the correct balance"
            acc2.balance() == amount("32.00")
    }


}
