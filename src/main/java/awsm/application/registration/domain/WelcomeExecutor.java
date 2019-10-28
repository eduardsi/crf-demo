package awsm.application.registration.domain;

import static awsm.infrastructure.middleware.ReturnsNothing.NOTHING;

import awsm.infrastructure.middleware.ReturnsNothing;
import awsm.infrastructure.middleware.impl.execution.Executor;
import org.springframework.stereotype.Component;

@Component
class WelcomeExecutor implements Executor<Welcome, ReturnsNothing> {

  private final Customer.Repository customers;

  private WelcomeExecutor(Customer.Repository customers) {
    this.customers = customers;
  }

  @Override
  public ReturnsNothing execute(Welcome cmd) {
    var customerId = new CustomerId(cmd.customerId);
    var customer = customers.singleBy(customerId);
    System.out.printf("Sending email to %s: Welcome to the Matrix, %s", customer.email(), customer.name());
    return NOTHING;
  }
}
