package awsm.domain.banking;

import static awsm.domain.offers.$.$;
import static awsm.infra.time.TimeMachine.today;
import static awsm.infra.time.TimeMachine.with;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.skyscreamer.jsonassert.JSONAssert.assertEquals;

import org.json.JSONException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.threeten.extra.Days;
import org.threeten.extra.MutableClock;

@DisplayName("bank account")
class BankAccountTest {

  private BankAccount account = new BankAccount(new WithdrawalLimit($("100.00")));

  private MutableClock clock = MutableClock.epochUTC();

  @BeforeEach
  void beforeEach() {
    with(clock);
  }

  @Test
  void provides_a_statement_for_a_given_time_interval() throws JSONException {
    clock.add(Days.ONE);
    account.deposit($("100.00"));

    clock.add(Days.ONE);
    var from = today();
    account.deposit($("99.00"));

    clock.add(Days.ONE);
    var to = today();
    account.withdraw($("98.00"));

    clock.add(Days.ONE);
    account.withdraw($("2.00"));

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
    var depositTx = account.deposit($("100.00"));
    assertThat(depositTx).isNotNull();

    var withdrawalTx = account.withdraw($("50.00"));
    assertThat(withdrawalTx).isNotNull();

    assertThat(account.balance()).isEqualTo($("50.00"));
  }

  @Test
  void cannot_withdraw_from_a_closed_account() {
    account.deposit($("100.00"));
    account.close(UnsatisfiedObligations.NONE);

    var e = assertThrows(IllegalStateException.class, () ->
        account.withdraw($("1.00")));
    assertThat(e).hasMessage("Account is closed.");
  }

  @Test
  void cannot_deposit_if_closed() {
    account.close(UnsatisfiedObligations.NONE);

    var e = assertThrows(IllegalStateException.class, () ->
        account.deposit($("100.00"))
    );
    assertThat(e).hasMessage("Account is closed.");
  }

  @Test
  void cannot_withdraw_more_funds_than_available() {
    var e = assertThrows(IllegalStateException.class, () ->
        account.withdraw($("1.00")));

    assertThat(e).hasMessage("Not enough funds available on your account.");
  }

  @Test
  void cannot_withdraw_more_than_allowed_by_the_daily_limit() {
    account.deposit($("1000.00"));

    var e = assertThrows(IllegalStateException.class, () -> account.withdraw($("101.00")));

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

}