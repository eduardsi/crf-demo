package awsm.infrastructure.middleware.impl.scheduler;

import static java.util.concurrent.CompletableFuture.allOf;
import static org.springframework.transaction.TransactionDefinition.PROPAGATION_REQUIRES_NEW;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.stream.Stream;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

@Component
class BatchOfScheduledCommands {

  private static final int BATCH_SIZE = 10;

  private static final Executor THREAD_POOL = Executors.newFixedThreadPool(BATCH_SIZE);

  private final ScheduledCommand.Repository repository;

  private final TransactionTemplate separateTx;

  public BatchOfScheduledCommands(ScheduledCommand.Repository repository, PlatformTransactionManager transactionManager) {
    this.repository = repository;
    this.separateTx = new TransactionTemplate(transactionManager);
    separateTx.setPropagationBehavior(PROPAGATION_REQUIRES_NEW);
  }

  @Transactional(noRollbackFor = CompletionException.class)
  @Scheduled(initialDelay = 5000, fixedDelay = 5000)
  public void executeAndWait() {
    allOf(
        execute().toArray(CompletableFuture[]::new)
    ).join();
  }

  private Stream<CompletableFuture> execute() {
    return repository
        .batchOfPending(BATCH_SIZE)
        .map(command -> command
            .executeIn(THREAD_POOL, repository, separateTx)
        );
  }
}
