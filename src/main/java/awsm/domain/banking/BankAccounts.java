package awsm.domain.banking;

import javax.persistence.EntityManager;
import org.springframework.stereotype.Component;

@Component
class BankAccounts {

  private final EntityManager entityManager;

  BankAccounts(EntityManager entityManager) {
    this.entityManager = entityManager;
  }

  public void add(BankAccount account) {
    entityManager.persist(account);
  }

  public BankAccount singleById(long id) {
    return entityManager.find(BankAccount.class, id);
  }

}
