package lightweight4j.features.administration;

import org.springframework.data.repository.Repository;

import java.util.Optional;

interface Administrators extends Repository<Administrator, Long> {

    Optional<Administrator> findById(Long id);

}
