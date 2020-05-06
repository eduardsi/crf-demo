package awsm.domain.banking;

import awsm.domain.banking.customer.Customer;

interface Approvable {
  void approve(Customer customer);
}
