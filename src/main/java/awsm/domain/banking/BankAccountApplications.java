package awsm.domain.banking;

import javax.persistence.EntityManager;
import org.springframework.stereotype.Repository;

@Repository
public class BankAccountApplications {

  private final EntityManager entityManager;

  BankAccountApplications(EntityManager entityManager) {
    this.entityManager = entityManager;
  }

  public void add(BankAccountApplication application) {
    var it = entityManager.getCriteriaBuilder();
    var criteria = it.createQuery(BankAccountApplication.class);


    var root = criteria.from(BankAccountApplication.class);
    System.out.println(root);

//    var where = criteria.where(it.equal(root.get(BankAccountApplication_.), 3);

    entityManager.createQuery(criteria);

//    entityManager.persist(application);
  }


}
