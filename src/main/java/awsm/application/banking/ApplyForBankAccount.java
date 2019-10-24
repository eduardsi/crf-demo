package awsm.application.banking;

import awsm.application.registration.FindACustomer;
import awsm.infrastructure.middleware.MiddlewareCommand;
import awsm.infrastructure.middleware.impl.react.Reaction;
import org.springframework.stereotype.Component;

class ApplyForBankAccount implements MiddlewareCommand<ApplyForBankAccount.ApplicationStatus> {

  private final String type;

  ApplyForBankAccount(String type) {
    this.type = type;
  }

  @Component
  static class Re implements Reaction<ApplyForBankAccount, ApplicationStatus> {

    @Override
    public ApplicationStatus react(ApplyForBankAccount cmd) {

      var customer = new FindACustomer().execute();
      System.out.println(customer);

      return new ApplicationStatus();
    }

  }

  static class ApplicationStatus {



  }

}
