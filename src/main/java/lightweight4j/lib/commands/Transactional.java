package lightweight4j.lib.commands;

import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

class Transactional implements PipelineBehavior {

    private final PipelineBehavior origin;
    private final TransactionTemplate tx;

    public Transactional(PlatformTransactionManager txManager, PipelineBehavior origin) {
        this.origin = origin;
        this.tx = new TransactionTemplate(txManager);
    }

    @Override
    public <R, C extends Command<R>> R mixIn(C command) {
        return tx.execute(txStatus -> origin.mixIn(command));
    }
}