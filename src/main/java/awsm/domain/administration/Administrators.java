package awsm.domain.administration;

import java.util.Optional;
import org.springframework.data.repository.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Transactional(propagation = Propagation.MANDATORY)
public interface Administrators extends Repository<Administrator, Long> {

  Optional<Administrator> findById(Long id);

  void save(Administrator administrator);

}
