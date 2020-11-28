package awsm.domain.banking

import awsm.domain.core.Amount
import com.github.javafaker.Faker
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.dao.OptimisticLockingFailureException
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.TransactionDefinition
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.support.TransactionTemplate
import spock.lang.Specification

import javax.persistence.EntityManager
import javax.persistence.LockModeType
import javax.persistence.PessimisticLockException
import java.util.concurrent.CountDownLatch

@DataJpaTest
@Transactional(propagation = Propagation.NOT_SUPPORTED)
class BankAccountLockingSpec extends Specification {

    @Autowired
    PlatformTransactionManager txManager

    @Autowired
    EntityManager entityManager

    @Delegate
    Faker fake = new Faker()

    def iban

    def defaultWithdrawalLimits

    def setup() {
        inANewTx {
            def holder = new AccountHolder(name().firstName(), name().lastName(), idNumber().valid(), internet().emailAddress())
            def withdrawalLimits = new WithdrawalLimits(Amount.of(1000.00), Amount.of(5000.00))
            def account = new BankAccount(holder, withdrawalLimits)
            account.open()
            entityManager.persist(account)

            this.iban = account.iban()
            this.defaultWithdrawalLimits = account.withdrawalLimits()
        }
    }

    def "without optimistic locking, I can override some else's successful commit"() {
        when: "I am modifying bank account and meanwhile someone else has modified the same account and committed the change"
        inANewTx {
            def account = entityManager.find(BankAccountWithoutOptimisticLock, iban)
            account.suspend()
            Thread.start {
                inANewTx {
                    def sameAccount = entityManager.find(BankAccountWithoutOptimisticLock, iban)
                    sameAccount.close(UnsatisfiedObligations.NONE)
                }
            }.join()
        }

        then: "After I commit, I completely override some else's committed change and someone else loses the update"
        assert inANewTx {
            def acc = entityManager.find(BankAccountWithoutOptimisticLock, iban)
            acc.isSuspended()
        }
    }

    def "without optimistic locking and dynamic update on, I can mess up the data"() {
        when: "I am modifying bank account and meanwhile someone else has modified the same account and committed the change"
        inANewTx {
            def account = entityManager.find(BankAccountWithDynamicUpdateAndWithoutOptimisticLock, iban)
            account.suspend()
            Thread.start {
                inANewTx {
                    def sameAccount = entityManager.find(BankAccountWithDynamicUpdateAndWithoutOptimisticLock, iban)
                    def withdrawalLimits = new WithdrawalLimits(Amount.of(10000.00), Amount.of(1000000.00))
                    sameAccount.lift(withdrawalLimits)
                    sameAccount.close(UnsatisfiedObligations.NONE)
                }
            }.join()
        }

        then: "When I commit, I partially override some else's successfully committed change. " +
                "Due to dynamic update turned on, I override only 'status' field, because it's the only field that has changed." +
                "So we ended up with 'partial' commit and messed up production data."
        assert inANewTx {
            def acc = entityManager.find(BankAccountWithDynamicUpdateAndWithoutOptimisticLock, iban)
            acc.isSuspended() && acc.withdrawalLimits() !== defaultWithdrawalLimits
        }
    }

    def "without optimistic locking, I can mess up the data even if dynamic update is disabled"() {
        when: "I am modifying bank account and meanwhile someone else has modified the same account and committed the change"
        inANewTx {
            def account = entityManager.find(BankAccountWithoutOptimisticLock, iban)
            account.deposit(Amount.of(100.00))
            Thread.start {
                inANewTx {
                    def sameAccount = entityManager.find(BankAccountWithoutOptimisticLock, iban)
                    sameAccount.deposit(Amount.of(1000.00))
                    sameAccount.close(UnsatisfiedObligations.NONE)
                }
            }.join()
        }

        then: "When I commit, I override some else's successfully committed change. " +
                "Moreover, I fuck up Transactions, not Account itself, because Hibernate think Account is not 'dirty'. " +
                "So we ended up with 'partial' commit and messed up production data."
            assert inANewTx {
                def acc = entityManager.find(BankAccountWithoutOptimisticLock, iban)
                acc.isClosed() && acc.balance() == Amount.of(100.00)
            }
    }

    def "with optimistic locking, no concurrent modification is allowed and the last transaction should roll back"() {
        when: "I am modifying bank account and meanwhile someone else has modified the same account and committed the change"
        inANewTx {
            def account = entityManager.find(BankAccount, iban)
            account.deposit(Amount.of(100.00))
            Thread.start {
                inANewTx {
                    def sameAccount = entityManager.find(BankAccount, iban)
                    sameAccount.deposit(Amount.of(100.00))
                }
            }.join()
        }

        then: "When I try to commit, I receive an exception"
        thrown(OptimisticLockingFailureException)
    }

    def "with pessimistic locking, no concurrent modification is allowed and the first transaction fails"() {
        def countdownLatch = new CountDownLatch(1)
        given: "Some is doing something with the bank account"
        Thread.start "someone", {
            inANewTx {
                def sameAccount = entityManager.find(BankAccountWithoutOptimisticLock, iban, LockModeType.PESSIMISTIC_READ)
                sameAccount.suspend()
                countdownLatch.countDown()
                sleep(5000)
            }
        }

        when: "I try to read the same bank account"
        inANewTx {
            countdownLatch.await()
            entityManager.find(BankAccountWithoutOptimisticLock, iban, LockModeType.PESSIMISTIC_READ)
        }

        then: "I should receive an exception"
        thrown(PessimisticLockException)
    }


    def <T> T inANewTx(Closure<T> closure) {
        def tx = new TransactionTemplate(txManager)
        tx.propagationBehavior = TransactionDefinition.PROPAGATION_REQUIRES_NEW
        tx.execute(closure) as T
    }

}
