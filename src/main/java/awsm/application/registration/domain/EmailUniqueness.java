package awsm.application.registration.domain;

import org.springframework.stereotype.Component;

interface EmailUniqueness {

  boolean guaranteed(String email);

  @Component
  class AcrossCustomers implements EmailUniqueness {

    private final Customer.Repository customerRepository;

    private AcrossCustomers(Customer.Repository customerRepository) {
      this.customerRepository = customerRepository;
    }

    @Override
    public boolean guaranteed(String email) {
      return !customerRepository.contains(email);
    }

  }

}
