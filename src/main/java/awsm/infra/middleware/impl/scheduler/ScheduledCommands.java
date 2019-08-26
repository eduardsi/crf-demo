package awsm.infra.middleware.impl.scheduler;

import static awsm.infra.middleware.impl.scheduler.ScheduledCommand.Status.PENDING;
import static awsm.infra.middleware.impl.scheduler.ScheduledCommand_.STATUS;
import static awsm.infra.middleware.impl.scheduler.ScheduledCommand_.TOUCH_TIMES;

import java.util.stream.Stream;
import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import org.springframework.stereotype.Component;

@Component
class ScheduledCommands {

  private final EntityManager entityManager;

  public ScheduledCommands(EntityManager entityManager) {
    this.entityManager = entityManager;
  }

  public void add(ScheduledCommand command) {
    entityManager.persist(command);
  }

  Stream<ScheduledCommand> listPendingTouchedLessThanThreeTimes(int limit) {
    var criteriaBuilder = entityManager.getCriteriaBuilder();
    var criteria = criteriaBuilder.createQuery(ScheduledCommand.class);
    var root = criteria.from(ScheduledCommand.class);

    criteriaBuilder.and(
        criteriaBuilder.lessThan(root.get(TOUCH_TIMES), 3),
        criteriaBuilder.equal(root.get(STATUS), PENDING));

    return entityManager
        .createQuery(criteria)
        .setLockMode(LockModeType.PESSIMISTIC_WRITE)
        .setMaxResults(limit)
        .getResultStream();
  }
}
