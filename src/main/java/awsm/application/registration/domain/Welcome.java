package awsm.application.registration.domain;

import static awsm.application.registration.domain.Welcome.ID;

import awsm.infrastructure.middleware.Command;
import awsm.infrastructure.middleware.CommandId;
import awsm.infrastructure.middleware.ReturnsNothing;

@CommandId(ID)
class Welcome implements Command<ReturnsNothing> {

  public static final String ID = "Welcome";

  final long customerId;

  Welcome(long customerId) {
    this.customerId = customerId;
  }

  @Override
  public String id() {
    return ID;
  }

}
