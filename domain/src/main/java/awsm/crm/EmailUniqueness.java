package awsm.crm;

import org.springframework.stereotype.Component;

interface EmailUniqueness {

  boolean guaranteed(Email email);

  @Component
  class AcrossCustomers implements EmailUniqueness {

    private final Customer.Repository customerRepository;

    private AcrossCustomers(Customer.Repository customerRepository) {
      this.customerRepository = customerRepository;
    }

    @Override
    public boolean guaranteed(Email email) {
      return !customerRepository.contains(email + "");
    }

  }

}
