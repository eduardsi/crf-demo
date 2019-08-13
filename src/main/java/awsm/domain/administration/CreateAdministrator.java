package awsm.domain.administration;

import awsm.domain.DomainEvent;
import awsm.domain.registration.Registration;
import org.springframework.stereotype.Component;

@Component
class CreateAdministrator implements DomainEvent.Listener<Registration> {

  private final Administrators administrators;

  private CreateAdministrator(Administrators administrators) {
    this.administrators = administrators;
  }

  @Override
  public void beforeCommit(Registration event) {
    var admin = new Administrator(event.member().id());
    administrators.save(admin);
  }

}
