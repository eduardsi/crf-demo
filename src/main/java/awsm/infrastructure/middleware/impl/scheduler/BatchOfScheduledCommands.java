package awsm.infrastructure.middleware.impl.scheduler;

import static awsm.infrastructure.middleware.impl.scheduler.ScheduledCommand.Status.PENDING;
import static java.util.concurrent.CompletableFuture.allOf;
import static jooq.tables.ScheduledCommand.SCHEDULED_COMMAND;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.stream.Stream;
import org.jooq.DSLContext;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
class BatchOfScheduledCommands {

  private static final int BATCH_SIZE = 10;

  private static final Executor THREAD_POOL = Executors.newFixedThreadPool(BATCH_SIZE);

  private final DSLContext dsl;

  public BatchOfScheduledCommands(DSLContext dsl) {
    this.dsl = dsl;
  }

  @Transactional(noRollbackFor = CompletionException.class)
  @Scheduled(initialDelay = 5000, fixedDelay = 5000)
  public void executeAndWait() {
    allOf(
        execute().toArray(CompletableFuture[]::new)
    ).join();
  }

  private Stream<CompletableFuture> execute() {
    return dsl
        .selectFrom(SCHEDULED_COMMAND)
        .where(SCHEDULED_COMMAND.RAN_TIMES.lessThan(3), SCHEDULED_COMMAND.STATUS.eq(PENDING.name()))
        .limit(BATCH_SIZE)
        .forUpdate()
        .fetchStream()
        .map(ScheduledCommand::new)
        .map(c -> c.executeIn(THREAD_POOL));
  }
}
