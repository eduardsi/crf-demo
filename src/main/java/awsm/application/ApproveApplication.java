package awsm.application;

import an.awesome.pipelinr.Command;
import an.awesome.pipelinr.Voidy;
import awsm.domain.banking.BankApplication;
import org.jooq.DSLContext;
import org.springframework.stereotype.Component;

import static awsm.infrastructure.ids.Ids.decoded;

public class ApproveApplication implements Command<Voidy> {

  private final String applicationId;

  public ApproveApplication(String applicationId) {
    this.applicationId = applicationId;
  }

  @Component
  public static class Handler implements Command.Handler<ApproveApplication, Voidy> {

    private final DSLContext db;

    public Handler(DSLContext db) {
      this.db = db;
    }

    @Override
    public Voidy handle(ApproveApplication command) {
      var applicationId = decoded(command.applicationId);
      var application = new BankApplication.Repo(db).findOne(applicationId);

      application.approve(db);

      return new Voidy();
    }

  }

}
