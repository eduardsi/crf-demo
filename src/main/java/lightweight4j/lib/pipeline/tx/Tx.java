package lightweight4j.lib.pipeline.tx;

import an.awesome.pipelinr.Command;
import an.awesome.pipelinr.PipelineStep;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

@Component
@Order(3)
class Tx implements PipelineStep {

    private final PlatformTransactionManager txManager;

    Tx(PlatformTransactionManager txManager) {
        this.txManager = txManager;
    }

    @Override
    public <R, C extends Command<R>> R invoke(C command, Next<R> next) {
        var tx = new TransactionTemplate(txManager);
        tx.setReadOnly(command instanceof ReadOnly);
        return tx.execute(txStatus -> next.invoke());
    }
}
