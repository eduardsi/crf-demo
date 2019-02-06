package lightweight4j.lib.commands;

import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.concurrent.CompletableFuture;

@Component
class Pipeline implements Now, Future {

    private final Router router;

    private final PlatformTransactionManager txManager;

    private final Ccid correlationId;

    public Pipeline(Router router, PlatformTransactionManager txManager, Ccid correlationId) {
        this.router = router;
        this.txManager = txManager;
        this.correlationId = correlationId;
    }

    @Override
    public <R, C extends Command<R>> R execute(C command) {
        var pipeline =
                new LogCorrelation(correlationId,
                        new Logging(
                                new Transactions(txManager,
                                        new Reactions(router))));

        return pipeline.process(command);
    }

    @Override
    public <R, C extends Command<R>> CompletableFuture<R> schedule(C command) {
        return CompletableFuture.supplyAsync(() -> this.execute(command));
    }

}
