package awsm.infrastructure.middleware.impl.tx;

import awsm.infrastructure.middleware.Command;
import awsm.infrastructure.middleware.Middleware;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

@Component
public class Tx implements Middleware {

  private final PlatformTransactionManager txManager;

  Tx(PlatformTransactionManager txManager) {
    this.txManager = txManager;
  }

  @Override
  public <R, C extends Command<R>> R invoke(C command, Next<R> next) {
    var tx = new TransactionTemplate(txManager);
    tx.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
    tx.setName("Tx for " + command.getClass().getSimpleName());
    tx.setReadOnly(command instanceof ReadOnly);
    return tx.execute(status -> next.invoke());
  }

}
