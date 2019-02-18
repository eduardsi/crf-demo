package lightweight4j.lib.pipeline.tx;

import an.awesome.pipelinr.Command;
import an.awesome.pipelinr.PipelineStep;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

@Component
@Order(3)
class CommandTransaction implements PipelineStep {

    private final TransactionTemplate tx;

    @Autowired
    CommandTransaction(PlatformTransactionManager txManager) {
        this.tx = new TransactionTemplate(txManager);
    }

    @Override
    public <R, C extends Command<R>> R invoke(C command, Next<R> next) {
        return tx.execute(txStatus -> next.invoke());
    }
}
