package lightweight4j.lib.domain;

import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

public interface SideEffect<T> {

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMPLETION)
    default void completed(T event) {

    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_ROLLBACK)
    default void rolledBack(T event) {

    }

}
