package awsm.domain.crm;

import awsm.domain.core.DomainRepository;
import org.springframework.data.repository.Repository;

@DomainRepository
public interface CustomerRepository extends Repository<Customer, String> {

  void save(Customer customer);

  boolean existsByEmail(String email);
}
