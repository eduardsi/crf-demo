package awsm.domain.registration;

import java.util.Optional;
import org.springframework.data.repository.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Transactional(propagation = Propagation.MANDATORY)
public interface Members extends Repository<Member, Long> {

  void save(Member member);

  Optional<Member> findById(Long id);

}
