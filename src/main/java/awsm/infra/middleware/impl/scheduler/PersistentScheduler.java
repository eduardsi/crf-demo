package awsm.infra.middleware.impl.scheduler;

import static awsm.infra.middleware.impl.scheduler.ScheduledCommand.Status.PENDING;
import static awsm.infra.middleware.impl.scheduler.ScheduledCommand_.RAN_TIMES;
import static awsm.infra.middleware.impl.scheduler.ScheduledCommand_.STATUS;
import static java.util.concurrent.CompletableFuture.allOf;
import static javax.persistence.LockModeType.PESSIMISTIC_WRITE;

import awsm.infra.middleware.Command;
import awsm.infra.middleware.Scheduler;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.stream.Stream;
import javax.persistence.EntityManager;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
class PersistentScheduler implements Scheduler {

  private static final int BATCH_SIZE = 10;

  private static final Executor THREAD_POOL = Executors.newFixedThreadPool(BATCH_SIZE);

  private final EntityManager entityManager;

  public PersistentScheduler(EntityManager entityManager) {
    this.entityManager = entityManager;
  }

  @Override
  public void schedule(Command command) {
    entityManager.persist(new ScheduledCommand(command));
  }

  @Transactional(noRollbackFor = CompletionException.class)
  @Scheduled(initialDelay = 5000, fixedDelay = 5000)
  public void executeBatch() {
    waitUntilCompletes(new Batch().execute());
  }

  private void waitUntilCompletes(Stream<CompletableFuture> futures) {
    allOf(
        futures.toArray(CompletableFuture[]::new)
    ).join();
  }

  private class Batch {

    private Stream<CompletableFuture> execute() {
      return list().map(c -> c.executeIn(THREAD_POOL));
    }

    private Stream<ScheduledCommand> list() {
      var it = entityManager.getCriteriaBuilder();
      var criteria = it.createQuery(ScheduledCommand.class);
      var root = criteria.from(ScheduledCommand.class);

      var where = criteria.where(
          it.and(
              it.lessThan(root.get(RAN_TIMES), 3),
              it.equal(root.get(STATUS), PENDING)));

      return entityManager
          .createQuery(where)
          .setLockMode(PESSIMISTIC_WRITE)
          .setMaxResults(BATCH_SIZE)
          .getResultStream();
    }
  }

  @Configuration
  @EnableScheduling
  static class SchedulerConfig {

  }

}
