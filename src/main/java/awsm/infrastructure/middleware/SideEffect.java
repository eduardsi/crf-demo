package awsm.infrastructure.middleware;

import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

public interface SideEffect<E> {

  @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
  default void beforeCommit(E event) {
  }

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  default void afterCommit(E event) {
  }

}
