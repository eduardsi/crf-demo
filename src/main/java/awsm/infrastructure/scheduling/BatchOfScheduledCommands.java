package awsm.infrastructure.scheduling;

import static java.util.concurrent.CompletableFuture.allOf;
import static java.util.concurrent.CompletableFuture.runAsync;
import static org.springframework.transaction.TransactionDefinition.PROPAGATION_REQUIRES_NEW;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.stream.Stream;

import an.awesome.pipelinr.Pipeline;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

@Component
class BatchOfScheduledCommands {

  private static final int BATCH_SIZE = 10;

  private static final Executor THREAD_POOL = Executors.newFixedThreadPool(BATCH_SIZE);

  private final ScheduledCommand.Repository repo;

  private final Pipeline pipeline;

  private final PlatformTransactionManager txManager;

  public BatchOfScheduledCommands(ScheduledCommand.Repository repo, Pipeline pipeline, PlatformTransactionManager txManager) {
    this.repo = repo;
    this.pipeline = pipeline;
    this.txManager = txManager;
  }

  @Transactional(readOnly = true)
  @Scheduled(initialDelay = 5000, fixedDelay = 5000)
  public void runAndWaitForAll() {
    allOf(
        run().toArray(CompletableFuture[]::new)
    ).join();
  }

  private Stream<CompletableFuture<Void>> run() {
    return repo
            .list(BATCH_SIZE)
            .map(this::work)
            .map(this::runInAPool);
  }

  private Runnable work(ScheduledCommand cmd) {
    return () -> newTx().executeWithoutResult(__ -> {
      cmd.execute(pipeline);
      repo.delete(cmd);
    });
  }

  private TransactionTemplate newTx() {
    var newTx = new TransactionTemplate(txManager);
    newTx.setPropagationBehavior(PROPAGATION_REQUIRES_NEW);
    return newTx;
  }

  private CompletableFuture<Void> runInAPool(Runnable runnable) {
    return runAsync(runnable, THREAD_POOL);
  }

}
