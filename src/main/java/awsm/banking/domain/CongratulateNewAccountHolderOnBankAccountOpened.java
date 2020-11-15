package awsm.banking.domain;

import awsm.infrastructure.scheduling.Scheduler;
import org.springframework.stereotype.Component;

@Component
public class CongratulateNewAccountHolderOnBankAccountOpened implements DomainEvent.SideEffect<BankAccountOpened> {

    private final Scheduler scheduler;

    CongratulateNewAccountHolderOnBankAccountOpened(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    @Override
    public void trigger(BankAccountOpened event) {
        var congratulateLater = new CongratulateNewAccountHolder(event.iban());
        scheduler.schedule(congratulateLater);
    }
}
