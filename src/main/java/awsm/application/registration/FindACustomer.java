package awsm.application.registration;

import awsm.infrastructure.middleware.MiddlewareCommand;

public class FindACustomer implements MiddlewareCommand<FindACustomer.FoundCustomer> {

  public static class FoundCustomer {
  }

}
