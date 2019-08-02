package lightweight4j.features.registration.impl;

import org.springframework.data.repository.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional(propagation = Propagation.MANDATORY)
interface Members extends Repository<Member, Long> {

    void save(Member member);

    Optional<Member> findById(Long id);

}
