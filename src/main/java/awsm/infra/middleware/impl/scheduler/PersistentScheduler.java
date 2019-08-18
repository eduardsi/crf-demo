package awsm.infra.middleware.impl.scheduler;

import static awsm.infra.middleware.impl.scheduler.ScheduledCommand.Status.PENDING;
import static awsm.infra.middleware.impl.scheduler.ScheduledCommand_.STATUS;
import static awsm.infra.middleware.impl.scheduler.ScheduledCommand_.TOUCHED_TIMES;
import static java.util.concurrent.CompletableFuture.allOf;

import awsm.infra.middleware.Command;
import awsm.infra.middleware.Scheduler;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.stream.Stream;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
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
    scheduledCommands.save(new ScheduledCommand(command));
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
    var batch = PageRequest.of(0, BATCH_SIZE);
    return scheduledCommands
        .findAll(new PendingAndTouchedLessThanThreeTimes(), batch)
        .stream()
        .map(c -> c.executeIn(THREAD_POOL));
  }

  private static class PendingAndTouchedLessThanThreeTimes implements Specification<ScheduledCommand> {

    @Override
    public Predicate toPredicate(Root<ScheduledCommand> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
      return cb.and(
              cb.lessThan(root.get(TOUCHED_TIMES), 3),
              cb.equal(root.get(STATUS), PENDING));
    }
  }
}
