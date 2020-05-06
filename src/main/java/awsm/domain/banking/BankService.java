package awsm.domain.banking;

import awsm.domain.banking.customer.Customer;
import awsm.infrastructure.middleware.DomainEvent;

public enum BankService implements Approvable {

  CHECKING_ACCOUNT {
    @Override
    public void approve(Customer customer) {
      new BankAccountApproved(customer).publish();
    }
  };

  public static class BankAccountApproved implements DomainEvent {

    private final Customer customer;

    public BankAccountApproved(Customer customer) {
      this.customer = customer;
    }

    public Customer customer() {
      return customer;
    }
  }
}

