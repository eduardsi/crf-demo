package awsm.application.registration;

import awsm.infrastructure.middleware.Command;

public class FindACustomer implements Command<FindACustomer.FoundCustomer> {

  public static class FoundCustomer {
  }

}
