package awsm.banking.domain.crm;

import org.springframework.data.repository.Repository;

import java.util.Optional;

@org.springframework.stereotype.Repository
public interface CustomerRepository extends Repository<Customer, String> {

    void save(Customer customer);

    Optional<Customer> findByEmail(String email);

}
