package awsm.banking.domain.crm;

import org.springframework.stereotype.Component;

public interface Uniqueness {

    Uniqueness ALWAYS_GUARANTEED = email -> true;

    boolean guaranteed(String email);

    @Component
    class AcrossCustomers implements Uniqueness {

        private final CustomerRepository customers;

        private AcrossCustomers(CustomerRepository customers) {
            this.customers = customers;
        }

        @Override
        public boolean guaranteed(String email) {
            return customers.findByEmail(email).isEmpty();
        }

    }

}
