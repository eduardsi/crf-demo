package awsm.domain.banking;

import org.springframework.data.repository.Repository;
import org.springframework.stereotype.Component;

@Component
public interface BankAccountRepository extends Repository<BankAccount, String> {

  BankAccount getOne(String iban);

  void save(BankAccount account);
}
