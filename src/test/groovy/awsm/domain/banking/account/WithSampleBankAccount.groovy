package awsm.domain.banking.account

import static awsm.domain.banking.commons.Amount.amount


trait WithSampleBankAccount {

    private def limits = new WithdrawalLimits(
            amount("100.00"),
            amount("1000.00")
    )

    BankAccount account = new BankAccount(limits)

}