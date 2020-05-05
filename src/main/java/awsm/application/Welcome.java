package awsm.application;

import an.awesome.pipelinr.Command;
import an.awesome.pipelinr.Voidy;
import awsm.infrastructure.middleware.scheduler.ScheduledCommandId;

import static awsm.application.Welcome.ID;

@ScheduledCommandId(ID)
class Welcome implements Command<Voidy> {

  static final String ID = "Welcome";

  final long customerId;

  Welcome(long customerId) {
    this.customerId = customerId;
  }

}
