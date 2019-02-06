package lightweight4j.lib.commands;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

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
        var pipes =
                new CorrelateLogs(correlationId,
                        new LogInputAndOutput(
                                new WrapInTx(txManager,
                                        new React(router))));

        return pipes.transport(command);
    }

    @Override
    public <R, C extends Command<R>> CompletableFuture<R> schedule(C command) {
        return CompletableFuture.supplyAsync(() -> this.execute(command));
    }

}

class LogInputAndOutput implements Pipe {

    private final Logger log = LoggerFactory.getLogger(LogInputAndOutput.class);

    private final Pipe origin;

    public LogInputAndOutput(Pipe origin) {
        this.origin = origin;
    }

    @Override
    public <R, C extends Command<R>> R transport(C command) {
        log.info(">>> {}", command.toString());
        var response = origin.transport(command);
        log.info("<<< {} ", response.toString());
        return response;
    }
}

class CorrelateLogs implements Pipe {

    private final Ccid ccid;
    private final Pipe origin;

    public CorrelateLogs(Ccid ccid, Pipe origin) {
        this.ccid = ccid;
        this.origin = origin;
    }

    @Override
    public <R, C extends Command<R>> R transport(C command) {
        try (var stashAutomatically = ccid.storeForLogging()) {
            return origin.transport(command);
        }
    }
}

class React implements Pipe {

    private final Router router;

    public React(Router router) {
        this.router = router;
    }

    @Override
    public <R, C extends Command<R>> R transport(C command) {
        var reaction = router.route(command);
        return reaction.react(command);
    }
}

class WrapInTx implements Pipe {

    private final Pipe origin;
    private final TransactionTemplate tx;

    public WrapInTx(PlatformTransactionManager txManager, Pipe origin) {
        this.origin = origin;
        this.tx = new TransactionTemplate(txManager);
    }

    @Override
    public <R, C extends Command<R>> R transport(C command) {
        return tx.execute(txStatus -> origin.transport(command));
    }
}
