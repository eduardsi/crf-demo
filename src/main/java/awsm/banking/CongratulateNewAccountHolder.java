package awsm.banking;

import an.awesome.pipelinr.Command;
import an.awesome.pipelinr.Voidy;
import awsm.infrastructure.scheduling.ScheduledCommandId;
import org.springframework.stereotype.Component;

import java.util.UUID;

import static awsm.banking.CongratulateNewAccountHolder.ID;

@ScheduledCommandId(ID)
class CongratulateNewAccountHolder implements Command<Voidy> {

  static final String ID = "Congratulations";

  final UUID accountHolderId;

  CongratulateNewAccountHolder(UUID accountHolderId) {
    this.accountHolderId = accountHolderId;
  }

  @Component
  static class Handler implements Command.Handler<CongratulateNewAccountHolder, Voidy> {

    private final Repository repository;

    private Handler(Repository repository) {
      this.repository = repository;
    }

    @Override
    public Voidy handle(CongratulateNewAccountHolder cmd) {
      var customer = repository.findOne(cmd.accountHolderId, Customer.class);
      System.out.printf("Congratulations,  %s. Thanks for using our services", customer.name());
      return new Voidy();
    }
  }

}
