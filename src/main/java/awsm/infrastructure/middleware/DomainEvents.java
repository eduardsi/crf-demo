package awsm.infrastructure.middleware;

import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class DomainEvents {

  private static Optional<DomainEvents> INSTANCE = Optional.empty();

  // http://bit.ly/forkjoinj8
  private final ExecutorService executor = Executors.newWorkStealingPool();

  private final TransactionTemplate tx;

  private final SideEffects sideEffects;

  public DomainEvents(SideEffects sideEffects, PlatformTransactionManager transactionManager) {
    this.sideEffects = sideEffects;
    this.tx = new TransactionTemplate(transactionManager);
    INSTANCE = Optional.of(this);
  }

  @SuppressWarnings("unchecked")
  public void put(DomainEvent event) {
    sideEffects
            .filteredBy(event)
            .forEach(sideEffect ->
                    runAsync(
                            inTx(() -> sideEffect.invoke(event))));
  }

  private void runAsync(Runnable runnable) {
    CompletableFuture.runAsync(runnable, executor);
  }

  private Runnable inTx(Runnable consumer) {
    return () -> tx.executeWithoutResult(status -> consumer.run());
  }


  public static DomainEvents INSTANCE() {
    return INSTANCE.orElseThrow(() -> new IllegalStateException("DomainEvents class is not initialized."));
  }
}

