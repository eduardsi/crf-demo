package awsm.domain.administration;

import awsm.domain.DomainEvent;
import awsm.domain.registration.RegistrationCompleted;
import org.springframework.stereotype.Component;

@Component
class CreateAdministrator implements DomainEvent.Listener<RegistrationCompleted> {

  private final Administrators administrators;

  private CreateAdministrator(Administrators administrators) {
    this.administrators = administrators;
  }

  @Override
  public void beforeCommit(RegistrationCompleted event) {
    var admin = new Administrator(event.member().id());
    administrators.save(admin);
  }

}
