package awsm.application.banking.domain;

import static awsm.application.banking.domain.BankAccount.Type.SAVINGS;
import static awsm.application.commons.money.Monetary.amount;
import static awsm.infrastructure.time.TimeMachine.today;
import static awsm.infrastructure.time.TimeMachine.with;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.skyscreamer.jsonassert.JSONAssert.assertEquals;

import org.json.JSONException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import org.threeten.extra.Days;
import org.threeten.extra.MutableClock;

@DisplayName("bank account")
class BankAccountTest {

  private BankAccount account = new BankAccount(SAVINGS, Iban.newlyGenerated(), new WithdrawalLimit(amount("100.00")));

  private MutableClock clock = MutableClock.epochUTC();

  @BeforeEach
  void beforeEach() {
    with(clock);
  }

  @Test
  void provides_a_statement_for_a_given_time_interval() throws JSONException {
    clock.add(Days.ONE);
    account.deposit(amount("100.00"));

    clock.add(Days.ONE);
    var from = today();
    account.deposit(amount("99.00"));

    clock.add(Days.ONE);
    var to = today();
    account.withdraw(amount("98.00"));

    clock.add(Days.ONE);
    account.withdraw(amount("2.00"));

    var actual = account.statement(from, to).json();
    var expected = """
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
    """;

    assertEquals(expected, actual, JSONCompareMode.STRICT);
  }

  @Test
  void supports_money_deposits_and_withdrawals() {
    var depositTx = account.deposit(amount("100.00"));
    assertThat(depositTx).isNotNull();

    var withdrawalTx = account.withdraw(amount("50.00"));
    assertThat(withdrawalTx).isNotNull();

    assertThat(account.balance()).isEqualTo(amount("50.00"));
  }

  @Test
  void cannot_withdraw_from_a_closed_account() {
    account.deposit(amount("100.00"));
    account.close(UnsatisfiedObligations.NONE);

    var e = assertThrows(IllegalStateException.class, () ->
        account.withdraw(amount("1.00")));
    assertThat(e).hasMessage("Account is closed.");
  }

  @Test
  void cannot_deposit_if_closed() {
    account.close(UnsatisfiedObligations.NONE);

    var e = assertThrows(IllegalStateException.class, () ->
        account.deposit(amount("100.00"))
    );
    assertThat(e).hasMessage("Account is closed.");
  }

  @Test
  void cannot_withdraw_more_funds_than_available() {
    var e = assertThrows(IllegalStateException.class, () ->
        account.withdraw(amount("1.00")));

    assertThat(e).hasMessage("Not enough funds available on your account.");
  }

  @Test
  void cannot_withdraw_more_than_allowed_by_the_daily_limit() {
    account.deposit(amount("1000.00"));

    var e = assertThrows(IllegalStateException.class, () -> account.withdraw(amount("101.00")));

    assertThat(e).hasMessage("Daily withdrawal limit (100.00) reached.");
  }

  @Test
  void cannot_be_closed_if_some_unsatisfied_obligations_exist() {
    var e = assertThrows(IllegalStateException.class, () -> account.close(new SomeUnsatisfiedObligations()));
    assertThat(e).hasMessage("Bank account cannot be closed because a holder has unsatified obligations");
  }

  private static class SomeUnsatisfiedObligations implements UnsatisfiedObligations {
    @Override
    public boolean exist() {
      return true;
    }
  }

  @SpringBootTest
  @Rollback
  @DisplayName("repository")
  @Nested
  class RepositoryTest {

    @Autowired
    PlatformTransactionManager txManager;

    @Autowired
    BankAccount.Repository repository;

    @Test
    void supports_saving_and_reading() {
      var tx = new TransactionTemplate(txManager);
      var limit = new WithdrawalLimit(amount("100.00"));
      var account = new BankAccount(SAVINGS, Iban.newlyGenerated(), limit);

      account.deposit(amount("50.00"));
      account.withdraw(amount("20.00"));

      tx.executeWithoutResult(whateverStatus -> account.saveNew(repository));

      var id = account.id();

      tx.executeWithoutResult(whateverStatus -> {
        var it = repository.singleBy(id);
        assertThat(it.balance()).isEqualTo(amount("30.00"));
        it.deposit(amount("70.00"));
        it.save(repository);
      });

      tx.executeWithoutResult(whateverStatus -> {
        var it = repository.singleBy(id);
        assertThat(it.balance()).isEqualTo(amount("100.00"));
      });

    }

  }
}