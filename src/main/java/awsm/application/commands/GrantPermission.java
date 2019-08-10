package awsm.application.commands;

import static awsm.infra.middleware.ReturnsNothing.NOTHING;

import awsm.domain.administration.Administrators;
import awsm.domain.administration.Permission;
import awsm.infra.middleware.Command;
import awsm.infra.middleware.ReturnsNothing;
import awsm.infra.middleware.impl.react.Reaction;
import org.springframework.stereotype.Component;

class GrantPermission implements Command<ReturnsNothing> {

  private final Long adminId;
  private final String operation;

  public GrantPermission(Long adminId, String operation) {
    this.adminId = adminId;
    this.operation = operation;
  }

  @Component
  static class Re implements Reaction<GrantPermission, ReturnsNothing> {

    private final Administrators admins;

    public Re(Administrators admins) {
      this.admins = admins;
    }

    @Override
    public ReturnsNothing react(GrantPermission cmd) {
      var admin = admins.findById(cmd.adminId).orElseThrow();
      admin.grant(Permission.toDo(cmd.operation));
      return NOTHING;
    }

  }
}
