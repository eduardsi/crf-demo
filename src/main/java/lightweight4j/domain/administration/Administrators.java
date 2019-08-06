package lightweight4j.domain.administration;

import org.springframework.data.repository.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional(propagation = Propagation.MANDATORY)
public interface Administrators extends Repository<Administrator, Long> {

    Optional<Administrator> findById(Long id);

    void save(Administrator administrator);

}
