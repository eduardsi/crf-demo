package awsm.application.registration.domain;

import static awsm.application.registration.domain.Welcome.ID;

import awsm.infrastructure.middleware.Command;
import awsm.infrastructure.middleware.CommandId;
import awsm.infrastructure.middleware.ReturnsNothing;
import com.fasterxml.jackson.annotation.JsonProperty;

@CommandId(ID)
class Welcome implements Command<ReturnsNothing> {

  public static final String ID = "Welcome";

  final long customerId;

  Welcome(@JsonProperty("customerId") long customerId) {
    this.customerId = customerId;
  }

  @Override
  public String id() {
    return ID;
  }

}
