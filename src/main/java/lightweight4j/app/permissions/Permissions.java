package lightweight4j.app.permissions;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
interface Permissions extends CrudRepository<Permission, String> {

    long countByNameStringIgnoreCase(String name);

}
