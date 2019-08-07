package awsm.domain.administration;

import an.awesome.pipelinr.Command;
import an.awesome.pipelinr.Voidy;
import org.springframework.stereotype.Component;

@Component
class CreateAdministratorHandler implements Command.Handler<CreateAdministrator, Voidy> {

  private final Administrators administrators;

  public CreateAdministratorHandler(Administrators administrators) {
    this.administrators = administrators;
  }

  @Override
  public Voidy handle(CreateAdministrator command) {
    var admin = new Administrator(command.memberId);
    administrators.save(admin);
    return new Voidy();
  }
}
