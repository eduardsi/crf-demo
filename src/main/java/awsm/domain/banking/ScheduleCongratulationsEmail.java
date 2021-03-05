package awsm.domain.banking;

import awsm.commands.CongratulateNewAccountHolderCommand;
import awsm.domain.core.DomainEvent;
import awsm.infrastructure.scheduling.Scheduler;
import org.springframework.stereotype.Component;

@Component
class ScheduleCongratulationsEmail implements DomainEvent.SideEffect<BankAccountOpened> {

  private final Scheduler scheduler;

  ScheduleCongratulationsEmail(Scheduler scheduler) {
    this.scheduler = scheduler;
  }

  @Override
  public void trigger(BankAccountOpened event) {
    var congratulateLater = new CongratulateNewAccountHolderCommand(event.iban(), event.date());
    scheduler.schedule(congratulateLater);
  }
}
