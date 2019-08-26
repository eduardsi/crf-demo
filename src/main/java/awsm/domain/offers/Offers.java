package awsm.domain.offers;

import java.util.Optional;
import javax.persistence.EntityManager;
import org.springframework.stereotype.Component;

@Component
public class Offers {

  private final EntityManager entityManager;

  public Offers(EntityManager entityManager) {
    this.entityManager = entityManager;
  }

  public Optional<Offer> singleById(Long id) {
    return Optional.ofNullable(entityManager.find(Offer.class, id));
  }

  public void add(Offer offer) {
    entityManager.persist(offer);
  }
}
