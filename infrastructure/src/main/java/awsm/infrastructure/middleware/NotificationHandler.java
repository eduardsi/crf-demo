package awsm.infrastructure.middleware;

import org.springframework.context.event.EventListener;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

public interface NotificationHandler<N extends Notification> {

  @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
  default void beforeCommit(N notification) {
  }

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  default void afterCommit(N notification) {
  }

  @EventListener
  default void immediately(N notification) {
  }

}
