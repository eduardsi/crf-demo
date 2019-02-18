package lightweight4j.features.membership.impl;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface Members extends CrudRepository<Member, String> {

}
