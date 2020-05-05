package awsm.application;

import an.awesome.pipelinr.Command;
import an.awesome.pipelinr.Voidy;
import awsm.domain.banking.customer.Customer;
import org.springframework.stereotype.Component;

@Component
class WelcomeNewCustomerHandler implements Command.Handler<Welcome, Voidy> {

  private final Customer.Repository customers;

  private WelcomeNewCustomerHandler(Customer.Repository customers) {
    this.customers = customers;
  }

  @Override
  public Voidy handle(Welcome cmd) {
    var customer = customers.findBy(cmd.customerId);
    System.out.printf("Sending email to %s: Welcome to the Matrix, %s", customer.email(), customer.name());
    return new Voidy();
  }
}
