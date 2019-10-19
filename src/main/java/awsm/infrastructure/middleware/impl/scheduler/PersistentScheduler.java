package awsm.infrastructure.middleware.impl.scheduler;

import static awsm.infrastructure.middleware.impl.scheduler.ScheduledCommand.Status.PENDING;
import static java.util.concurrent.CompletableFuture.allOf;

import awsm.infrastructure.middleware.Command;
import awsm.infrastructure.middleware.Scheduler;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.stream.Stream;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
class PersistentScheduler implements Scheduler {

  private static final int BATCH_SIZE = 10;

  private static final Executor THREAD_POOL = Executors.newFixedThreadPool(BATCH_SIZE);

  private final JdbcTemplate jdbc;

  public PersistentScheduler(JdbcTemplate jdbc) {
    this.jdbc = jdbc;
  }

  @Override
  public void schedule(Command command) {
    new ScheduledCommand(command).saveNew(jdbc);
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

      return jdbc.query("select * from scheduled_command c where c.ran_times < ? and c.status = ? limit ? for update",
          (rs, rowNo) -> new ScheduledCommand(rs), 3, PENDING.name(), BATCH_SIZE).stream();


//      var it = entityManager.getCriteriaBuilder();
//      var criteria = it.createQuery(ScheduledCommand.class);
//      var root = criteria.from(ScheduledCommand.class);
//
//      var where = criteria.where(
//          it.and(
//              it.lessThan(root.get(RAN_TIMES), 3),
//              it.equal(root.get(STATUS), PENDING)));
//
//      return entityManager
//          .createQuery(where)
//          .setLockMode(PESSIMISTIC_WRITE)
//          .setMaxResults(BATCH_SIZE)
//          .getResultStream();
    }
  }

  @Configuration
  @EnableScheduling
  static class SchedulerConfig {

  }

}
