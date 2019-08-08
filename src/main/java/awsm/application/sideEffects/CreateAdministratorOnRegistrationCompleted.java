package awsm.application.sideEffects;

import an.awesome.pipelinr.Command;
import an.awesome.pipelinr.Voidy;
import awsm.domain.administration.Administrator;
import awsm.domain.administration.Administrators;
import awsm.domain.registration.RegistrationCompleted;
import awsm.infra.modeling.SideEffect;
import awsm.infra.pipeline.ExecutableCommand;
import org.springframework.stereotype.Component;

@Component
class CreateAdministratorOnRegistrationCompleted implements SideEffect<RegistrationCompleted> {

  @Override
  public void beforeCommit(RegistrationCompleted event) {
    var memberId = event.memberId();
    new CreateAdministrator(memberId).execute();
  }

  private static class CreateAdministrator extends ExecutableCommand<Voidy> {

    private final Long memberId;

    private CreateAdministrator(Long memberId) {
      this.memberId = memberId;
    }

    @Component
    static class Handler implements Command.Handler<CreateAdministrator, Voidy> {

      private final Administrators administrators;

      public Handler(Administrators administrators) {
        this.administrators = administrators;
      }

      @Override
      public Voidy handle(CreateAdministrator command) {
        var admin = new Administrator(command.memberId);
        administrators.save(admin);
        return new Voidy();
      }
    }

  }
}
