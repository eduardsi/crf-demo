package awsm.domain.banking;

import awsm.application.CongratulateNewAccountHolder;
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
    var congratulateLater = new CongratulateNewAccountHolder(event.iban());
    scheduler.schedule(congratulateLater);
  }
}
