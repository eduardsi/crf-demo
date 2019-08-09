package awsm.infra.pipeline.tx;

import an.awesome.pipelinr.Command;
import an.awesome.pipelinr.PipelineStep;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

@Component
@Order(5)
class Tx implements PipelineStep {

  private final PlatformTransactionManager txManager;

  public Tx(PlatformTransactionManager txManager) {
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
