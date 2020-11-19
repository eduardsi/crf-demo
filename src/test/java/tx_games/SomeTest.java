package tx_games;

import awsm.AwesomeApp;
import awsm.domain.banking.AccountHolder;
import awsm.domain.banking.BankAccount;
import awsm.domain.banking.BankAccountRepository;
import awsm.domain.banking.WithdrawalLimits;
import awsm.domain.core.Amount;
import org.hamcrest.CoreMatchers;
import org.junit.internal.matchers.ThrowableCauseMatcher;
import org.junit.jupiter.api.Test;
import org.simplejavamail.api.mailer.Mailer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.env.Environment;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.concurrent.CompletionException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(classes = AwesomeApp.class)
public class SomeTest {

    @Autowired
    PlatformTransactionManager txManager;

    @Autowired
    BankAccountRepository accounts;

    @Autowired
    Environment env;

    @MockBean
    Mailer mailer;

    @Test
    public void test() throws InterruptedException {
        var threads = new Threads(2);
        var tx = new Transactions(txManager);

        var offerId = tx.wrap(() -> {
            AccountHolder holder = new AccountHolder("Eduards", "Sizovs", "210688-10425", "eduards@sizovs.net");
            WithdrawalLimits withdrawalLimits = WithdrawalLimits.defaults(env);
            BankAccount account = new BankAccount(holder, withdrawalLimits);
            account.open();
            accounts.save(account);
            return account.iban();
        }).get();

        threads.spinOff(tx.wrap(() -> {
            var account = accounts.getOne(offerId);
            account.deposit(Amount.of("500.00"));
            threads.sync();
        }));

        threads.spinOff(tx.wrap(() -> {
            var account = accounts.getOne(offerId);
            account.deposit(Amount.of("400.00"));
            threads.sync();
        }));

        Thread.sleep(3000);
        tx.wrap(() -> {
            var account = accounts.getOne(offerId);
            System.out.println(account.balance());
        }).run();

        var e = assertThrows(CompletionException.class, threads::waitForAll);
        assertThat(e, ThrowableCauseMatcher.hasCause(CoreMatchers.instanceOf(ObjectOptimisticLockingFailureException.class)));
    }

}
