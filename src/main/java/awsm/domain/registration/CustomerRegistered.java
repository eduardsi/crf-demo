package awsm.domain.registration;

import awsm.domain.DomainEvent;

public class CustomerRegistered implements DomainEvent {

  private final Customer customer;

  CustomerRegistered(Customer customer) {
    this.customer = customer;
  }

  public Customer customer() {
    return customer;
  }
}
