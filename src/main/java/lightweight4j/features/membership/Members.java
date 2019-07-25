package lightweight4j.features.membership;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
interface Members extends CrudRepository<Member, String> {

}
