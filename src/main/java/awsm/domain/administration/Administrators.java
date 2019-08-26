package awsm.domain.administration;

import java.util.Optional;
import javax.persistence.EntityManager;
import org.springframework.stereotype.Component;

@Component
public class Administrators  {

  private final EntityManager entityManager;

  public Administrators(EntityManager entityManager) {
    this.entityManager = entityManager;
  }

  public Optional<Administrator> singleById(Long id) {
    return Optional.ofNullable(entityManager.find(Administrator.class, id));
  }

  public void add(Administrator administrator) {
    entityManager.persist(administrator);
  }
}

