package awsm.banking

import static Money.money

trait WithSampleBankAccount {

    private def currency = new Currency("EUR")
    private def limits = new WithdrawalLimits(
            eur("100.00").amount(),
            eur("1000.00").amount()
    )

    BankAccount account = new BankAccount(currency, limits)

    Money eur(String amount) {
        money(amount, currency.toString())
    }

}