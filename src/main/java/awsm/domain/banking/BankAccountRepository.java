package awsm.domain.banking;

import org.springframework.data.repository.Repository;
import org.springframework.stereotype.Component;

@Component
public interface BankAccountRepository extends Repository<BankAccount, String> {

  BankAccount getOne(String iban);

  long countByTransactionStatus(Transaction.Status status);

  void save(BankAccount account);
}
