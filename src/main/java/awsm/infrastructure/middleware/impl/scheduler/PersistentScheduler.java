package awsm.infrastructure.middleware.impl.scheduler;

import static awsm.infrastructure.middleware.impl.scheduler.ScheduledCommand.Status.PENDING;
import static jooq.tables.ScheduledCommand.SCHEDULED_COMMAND;
import static java.util.concurrent.CompletableFuture.allOf;

import awsm.infrastructure.middleware.Command;
import awsm.infrastructure.middleware.Scheduler;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.stream.Stream;
import javax.sql.DataSource;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
class PersistentScheduler implements Scheduler {

  private static final int BATCH_SIZE = 10;

  private static final Executor THREAD_POOL = Executors.newFixedThreadPool(BATCH_SIZE);

  private final DataSource dataSource;

  public PersistentScheduler(DataSource dataSource) {
    this.dataSource = dataSource;
  }

  @Override
  public void schedule(Command command) {
    new ScheduledCommand(command).saveNew(dataSource);
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
      return DSL.using(dataSource, SQLDialect.POSTGRES)
          .selectFrom(SCHEDULED_COMMAND)
          .where(SCHEDULED_COMMAND.RAN_TIMES.lessThan(3), SCHEDULED_COMMAND.STATUS.eq(PENDING.name()))
          .limit(BATCH_SIZE)
          .forUpdate()
          .fetchStream()
          .map(ScheduledCommand::new);
    }
  }

  @Configuration
  @EnableScheduling
  static class SchedulerConfig {

  }

}
