package awsm.application;

import an.awesome.pipelinr.Command;
import an.awesome.pipelinr.Voidy;
import awsm.domain.banking.customer.Customer;
import awsm.infrastructure.middleware.scheduler.ScheduledCommandId;
import org.jooq.DSLContext;
import org.springframework.stereotype.Component;

import static awsm.application.Welcome.ID;

@ScheduledCommandId(ID)
class Welcome implements Command<Voidy> {

  static final String ID = "Welcome";

  final long customerId;

  Welcome(long customerId) {
    this.customerId = customerId;
  }

  @Component
  static class Handler implements Command.Handler<Welcome, Voidy> {

    private final DSLContext db;

    private Handler(DSLContext db) {
      this.db = db;
    }

    @Override
    public Voidy handle(Welcome cmd) {
      var customer = new Customer.Repo(db).findBy(cmd.customerId);
      System.out.printf("Sending email to %s: Welcome to the Matrix, %s", customer.email(), customer.name());
      return new Voidy();
    }
  }

}
