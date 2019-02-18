package lightweight4j.features.permissions.impl;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface Permissions extends CrudRepository<Permission, String> {

    long countByNameStringIgnoreCase(String name);

}
