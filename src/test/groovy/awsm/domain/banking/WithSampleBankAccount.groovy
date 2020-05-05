package awsm.domain.banking

import static awsm.domain.banking.Amount.amount

trait WithSampleBankAccount {

    private def limits = new WithdrawalLimits(
            amount("100.00"),
            amount("1000.00")
    )

    BankAccount account = new BankAccount(limits)

}