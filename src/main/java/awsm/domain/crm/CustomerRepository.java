package awsm.domain.crm;

import org.springframework.data.repository.Repository;

@org.springframework.stereotype.Repository
public interface CustomerRepository extends Repository<Customer, String> {

    void save(Customer customer);

    boolean existsByEmail(String email);

}
