package net.sizovs.crf.backbone;

import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;

@Component
class Pipeline implements Now {

    private final Router router;

    private final PlatformTransactionManager txManager;

    private final CommandCorrelationId correlationId;

    public Pipeline(Router router, PlatformTransactionManager txManager, CommandCorrelationId correlationId) {
        this.router = router;
        this.txManager = txManager;
        this.correlationId = correlationId;
    }

    @Override
    public <R extends Command.R, C extends Command<R>> R execute(C command) {
        Now pipeline =
                new Correlable(correlationId,
                        new Loggable(
                                new Transactional(txManager,
                                        new Reacting(router))));

        return pipeline.execute(command);
    }

}
