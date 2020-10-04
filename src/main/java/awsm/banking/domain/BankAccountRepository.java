package awsm.banking.domain;

import org.springframework.data.repository.Repository;

@org.springframework.stereotype.Repository
public interface BankAccountRepository extends Repository<BankAccount, String> {

    BankAccount getOne(String iban);

}
