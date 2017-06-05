package net.sizovs.crf.services.membership;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface Members extends CrudRepository<Member, String> {

}
