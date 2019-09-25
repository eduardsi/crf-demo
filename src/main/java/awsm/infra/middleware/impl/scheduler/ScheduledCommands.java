package awsm.infra.middleware.impl.scheduler;

import static awsm.infra.middleware.impl.scheduler.ScheduledCommand.Status.PENDING;
import static awsm.infra.middleware.impl.scheduler.ScheduledCommand_.STATUS;
import static awsm.infra.middleware.impl.scheduler.ScheduledCommand_.TOUCH_TIMES;
import static javax.persistence.LockModeType.PESSIMISTIC_WRITE;

import java.util.stream.Stream;
import javax.persistence.EntityManager;
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

  Stream<ScheduledCommand> list(int limit) {
    var it = entityManager.getCriteriaBuilder();
    var criteria = it.createQuery(ScheduledCommand.class);
    var root = criteria.from(ScheduledCommand.class);

    var where = criteria.where(
        it.and(
          it.lessThan(root.get(TOUCH_TIMES), 3),
          it.equal(root.get(STATUS), PENDING)));

    return entityManager
        .createQuery(where)
        .setLockMode(PESSIMISTIC_WRITE)
        .setMaxResults(limit)
        .getResultStream();
  }
}
