package awsm.domain.offers;

import java.util.Optional;
import org.springframework.data.repository.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Transactional(propagation = Propagation.MANDATORY)
public interface Offers extends Repository<Offer, Long> {

  Optional<Offer> findById(Long id);

  void save(Offer offer);

}
