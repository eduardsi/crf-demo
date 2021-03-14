package awsm.domain.banking;

import awsm.domain.core.DomainRepository;
import org.springframework.data.repository.Repository;

@DomainRepository
public interface BankAccountRepository extends Repository<BankAccount, String> {

  BankAccount getOne(String iban);

  void save(BankAccount account);
}
