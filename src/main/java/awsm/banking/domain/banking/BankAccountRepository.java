package awsm.banking.domain.banking;

import org.springframework.data.repository.Repository;

@org.springframework.stereotype.Repository
public interface BankAccountRepository extends Repository<BankAccount, String> {

    BankAccount getOne(String iban);

    void save(BankAccount account);
}
