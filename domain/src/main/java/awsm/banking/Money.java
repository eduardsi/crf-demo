package awsm.banking;

class Money {

  private final Amount amount;

  private final Currency currency;

  private Money(Amount amount, Currency currency) {
    this.amount = amount;
    this.currency = currency;
  }

  Amount amount() {
    return amount;
  }

  Currency currency() {
    return currency;
  }

  static Money money(String amount, String currency) {
    return new Money(
        Amount.amount(amount), new Currency(currency)
    );
  }


}
