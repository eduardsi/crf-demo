package awsm.application.customer;

import awsm.domain.banking.BankAccount;
import awsm.domain.banking.BankAccountApplication;
import awsm.domain.banking.BankAccountApplications;
import awsm.domain.registration.Customers;
import awsm.infra.middleware.Command;
import awsm.infra.middleware.impl.react.Reaction;
import org.springframework.stereotype.Component;

class ApplyForBankAccount implements Command<ApplyForBankAccount.ApplicationStatus> {

  private final Long customerId;
  private final String type;

  ApplyForBankAccount(Long customerId, String type) {
    this.customerId = customerId;
    this.type = type;
  }

  @Component
  static class Re implements Reaction<ApplyForBankAccount, ApplicationStatus> {

    private final BankAccountApplications applications;
    private final Customers customers;

    Re(BankAccountApplications applications, Customers customers) {
      this.applications = applications;
      this.customers = customers;
    }

    @Override
    public ApplicationStatus react(ApplyForBankAccount cmd) {

      var customer = customers.singleById(cmd.customerId).orElseThrow();
      var accountType = BankAccount.Type.valueOf(cmd.type);

      var application = new BankAccountApplication(customer, accountType);
      applications.add(application);

      return new ApplicationStatus();
    }

  }

  static class ApplicationStatus {



  }

}
