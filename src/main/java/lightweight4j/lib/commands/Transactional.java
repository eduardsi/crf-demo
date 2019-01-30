package lightweight4j.lib.commands;

import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;


class Transactional implements Now {

    private final Now origin;
    private final TransactionTemplate tx;

    public Transactional(PlatformTransactionManager txManager, Now origin) {
        this.origin = origin;
        this.tx = new TransactionTemplate(txManager);
    }

    @Override
    public <R, C extends Command<R>> R execute(C command) {
        return tx.execute(txStatus -> origin.execute(command));
    }
}