package awsm.domain.registration;

import java.util.Optional;
import javax.persistence.EntityManager;
import org.springframework.stereotype.Component;

@Component
public class Customers {

  private final EntityManager entityManager;

  public Customers(EntityManager entityManager) {
    this.entityManager = entityManager;
  }

  public void add(Customer customer) {
    entityManager.persist(customer);
  }

  public Optional<Customer> singleById(Long id) {
    return Optional.ofNullable(entityManager.find(Customer.class, id));
  }

  Optional<Customer> singleByEmail(String email) {

    var it = entityManager.getCriteriaBuilder();
    var criteria = it.createQuery(Customer.class);
    var root = criteria.from(Customer.class);

    var where = criteria.where(
        it.equal(
            root.get(Customer_.EMAIL).get(Email_.EMAIL), email));

    return entityManager
        .createQuery(where)
        .getResultStream()
        .findFirst();

  }

}
