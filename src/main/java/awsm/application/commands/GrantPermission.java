package awsm.application.commands;

import an.awesome.pipelinr.Voidy;
import awsm.domain.administration.Administrators;
import awsm.domain.administration.Permission;
import awsm.infra.pipeline.ExecutableCommand;
import org.springframework.stereotype.Component;

public class GrantPermission extends ExecutableCommand<Voidy> {

  private final Long adminId;
  private final String operation;

  public GrantPermission(Long adminId, String operation) {
    this.adminId = adminId;
    this.operation = operation;
  }

  @Component
  static class GrantPermissionHandler implements Handler<GrantPermission, Voidy> {

    private final Administrators admins;

    public GrantPermissionHandler(Administrators admins) {
      this.admins = admins;
    }

    @Override
    public Voidy handle(GrantPermission cmd) {
      var admin = admins.findById(cmd.adminId).orElseThrow();
      admin.grant(Permission.toDo(cmd.operation));
      return new Voidy();
    }

  }
}
