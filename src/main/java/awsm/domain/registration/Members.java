package awsm.domain.registration;

import java.util.Optional;
import javax.persistence.EntityManager;
import org.hibernate.Session;
import org.springframework.stereotype.Component;

@Component
public class Members {

  private final EntityManager entityManager;

  public Members(EntityManager entityManager) {
    this.entityManager = entityManager;
  }

  public void add(Member member) {
    entityManager.persist(member);
  }

  public Optional<Member> singleById(Long id) {
    return Optional.ofNullable(entityManager.find(Member.class, id));
  }

  Optional<Member> singleByEmail(Email email) {
    return entityManager.unwrap(Session.class)
        .bySimpleNaturalId(Member.class)
        .loadOptional(email);
  }

}
