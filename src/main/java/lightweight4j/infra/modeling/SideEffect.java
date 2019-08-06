package lightweight4j.infra.modeling;

import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

public interface SideEffect<T extends Event> {

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    default void afterCommit(T event) {

    }

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    default void beforeCommit(T event) {

    }

}
