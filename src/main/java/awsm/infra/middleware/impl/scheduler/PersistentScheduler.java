package awsm.infra.middleware.impl.scheduler;

import static java.util.concurrent.CompletableFuture.allOf;

import awsm.infra.middleware.Command;
import awsm.infra.middleware.Scheduler;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.stream.Stream;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
class PersistentScheduler implements Scheduler {

  private static final int BATCH_SIZE = 10;

  private static final Executor THREAD_POOL = Executors.newFixedThreadPool(BATCH_SIZE);

  private final ScheduledCommands scheduledCommands;

  public PersistentScheduler(ScheduledCommands scheduledCommands) {
    this.scheduledCommands = scheduledCommands;
  }

  @Override
  public void schedule(Command command) {
    scheduledCommands.add(new ScheduledCommand(command));
  }

  @Transactional(noRollbackFor = CompletionException.class)
  @Scheduled(initialDelay = 5000, fixedDelay = 5000)
  public void executeBatch() {
    waitUntilCompletes(batch());
  }

  private void waitUntilCompletes(Stream<CompletableFuture> futures) {
    allOf(
        futures.toArray(CompletableFuture[]::new)
    ).join();
  }

  private Stream<CompletableFuture> batch() {
    return scheduledCommands
        .listPendingTouchedLessThanThreeTimes(BATCH_SIZE)
        .map(c -> c.executeIn(THREAD_POOL));
  }

}
