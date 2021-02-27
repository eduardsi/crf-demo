package awsm.domain.crm;

import org.springframework.stereotype.Component;

public interface Uniqueness {

  boolean guaranteed(String email);

  @Component
  class AcrossCustomers implements Uniqueness {

    private final CustomerRepository customersRepo;

    private AcrossCustomers(CustomerRepository customersRepo) {
      this.customersRepo = customersRepo;
    }

    @Override
    public boolean guaranteed(String email) {
      return !customersRepo.existsByEmail(email);
    }
  }
}
