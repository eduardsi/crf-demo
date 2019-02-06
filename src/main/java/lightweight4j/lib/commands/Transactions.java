package lightweight4j.lib.commands;

import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

class Transactions implements Now.Filter {

    private final Now.Filter origin;
    private final TransactionTemplate tx;

    public Transactions(PlatformTransactionManager txManager, Now.Filter origin) {
        this.origin = origin;
        this.tx = new TransactionTemplate(txManager);
    }

    @Override
    public <R, C extends Command<R>> R process(C command) {
        return tx.execute(txStatus -> origin.process(command));
    }
}