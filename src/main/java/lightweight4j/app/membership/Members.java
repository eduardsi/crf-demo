package lightweight4j.app.membership;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface Members extends CrudRepository<Member, String> {

}
