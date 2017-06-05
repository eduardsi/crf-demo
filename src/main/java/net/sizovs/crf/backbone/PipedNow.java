package net.sizovs.crf.backbone;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;

@Component
class PipedNow implements Now {

    private final Router router;

    private final PlatformTransactionManager txManager;

    private final CommandCorrelationId correlationId;

    @Autowired
    public PipedNow(Router router, PlatformTransactionManager txManager, CommandCorrelationId correlationId) {
        this.router = router;
        this.txManager = txManager;
        this.correlationId = correlationId;
    }

    @Override
    public <R extends Command.R, C extends Command<R>> R execute(C command) {
        Now pipe =
                new Correlable(correlationId,
                        new Loggable(
                                new Transactional(txManager,
                                        new Reacting(router))));

        return pipe.execute(command);
    }

}
