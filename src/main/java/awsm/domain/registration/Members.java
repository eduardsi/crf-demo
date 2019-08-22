package awsm.domain.registration;

import java.util.Optional;
import javax.persistence.EntityManager;
import org.hibernate.Session;
import org.springframework.data.repository.Repository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Transactional(propagation = Propagation.MANDATORY)
@Component
public class Members implements Repository<Member, Long> {

  private final EntityManager entityManager;

  public Members(EntityManager entityManager) {
    this.entityManager = entityManager;
  }

  public void save(Member member) {
    entityManager.persist(member);
  }

  public Optional<Member> findById(Long id) {
    return Optional.ofNullable(entityManager.find(Member.class, id));
  }

  Optional<Member> findByEmail(Email email) {
    return entityManager.unwrap(Session.class)
        .bySimpleNaturalId(Member.class)
        .loadOptional(email);
  }

}
