package awsm.domain.banking;

import static awsm.infra.time.TimeMachine.clock;
import static awsm.infra.time.TimeMachine.freezeEpoch;
import static awsm.infra.time.TimeMachine.offset;
import static java.time.Duration.ofDays;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.skyscreamer.jsonassert.JSONAssert.assertEquals;

import awsm.domain.offers.DecimalNumber;
import java.time.LocalDate;
import org.json.JSONException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONCompareMode;

@DisplayName("bank account")
class BankAccountTest {

  @BeforeEach
  void beforeEach() {
    freezeEpoch();
  }

  @Test
  void provides_a_statement_for_a_given_time_interval() throws JSONException {
    var account = new BankAccount(hundredADay());

    offset(ofDays(1));
    account.deposit(new DecimalNumber("100.00"));

    offset(ofDays(1));
    var from = LocalDate.now(clock());
    account.deposit(new DecimalNumber("99.00"));

    offset(ofDays(1));
    var to = LocalDate.now(clock());
    account.withdraw(new DecimalNumber("98.00"));

    offset(ofDays(1));
    account.withdraw(new DecimalNumber("2.00"));

    var statement = account.statement(from, to).json();
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
            "time": "1970-01-03T03:00:00",
            "deposit": "99.00",
            "withdrawal": "0.00",
            "balance": "199.00"
          },
          {
            "time": "1970-01-04T03:00:00",
            "deposit": "0.00",
            "withdrawal" :"98.00",
            "balance": "101.00"
          }
        ]
       }
    """;

    assertEquals(expected, statement, JSONCompareMode.STRICT);
  }

  @Test
  void supports_money_deposits_and_withdrawals() {

    var account = new BankAccount(hundredADay());

    var depositTx = account.deposit(new DecimalNumber("100.00"));
    assertThat(depositTx).isNotNull();

    var withdrawalTx = account.withdraw(new DecimalNumber("50.00"));
    assertThat(withdrawalTx).isNotNull();

    assertThat(account.balance()).isEqualTo(new DecimalNumber("50.00"));
  }

  @Test
  void cannot_withdraw_from_a_closed_account() {
    var account = new BankAccount(hundredADay());
    account.deposit(new DecimalNumber("100.00"));
    account.close();

    var e = assertThrows(IllegalStateException.class, () ->
        account.withdraw(new DecimalNumber("1.00")));
    assertThat(e).hasMessage("Cannot withdraw funds from closed account.");
  }

  @Test
  void cannot_deposit_if_closed() {
    var account = new BankAccount(hundredADay());
    account.close();

    var e = assertThrows(IllegalStateException.class, () ->
        account.deposit(new DecimalNumber("100.00"))
    );
    assertThat(e).hasMessage("Cannot deposit funds to closed account.");
  }

  @Test
  void cannot_withdraw_more_funds_than_available() {
    var account = new BankAccount(hundredADay());

    var e = assertThrows(IllegalStateException.class, () ->
        account.withdraw(new DecimalNumber("1.00")));

    assertThat(e).hasMessage("Cannot withdraw more funds than available on your account.");
  }

  @Test
  void cannot_withdraw_more_than_allowed_by_the_daily_limit() {
    var account = new BankAccount(hundredADay());
    account.deposit(new DecimalNumber("1000.00"));

    var e = assertThrows(IllegalStateException.class, () ->
        account.withdraw(new DecimalNumber("101.00")));

    assertThat(e).hasMessage("Cannot withdraw funds. Daily withdrawal limit (100.00) reached.");
  }

  private WithdrawalLimit hundredADay() {
    var dailyLimit = new DecimalNumber("100.00");
    return new WithdrawalLimit(dailyLimit);
  }

}