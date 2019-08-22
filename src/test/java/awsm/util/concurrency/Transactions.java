package awsm.util.concurrency;

import java.util.function.Supplier;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

public class Transactions {

  private final PlatformTransactionManager txManager;

  public Transactions(PlatformTransactionManager txManager) {
    this.txManager = txManager;
  }

  public <R> Supplier<R> wrap(Supplier<R> suppliesSomething) {
    var tx = new TransactionTemplate(txManager);
    return () -> tx.execute(status -> suppliesSomething.get());
  }

  public Runnable wrap(Runnable suppliesNothing) {
    var tx = new TransactionTemplate(txManager);
    return () -> tx.execute(new TransactionCallbackWithoutResult() {
      @Override
      protected void doInTransactionWithoutResult(TransactionStatus status) {
        suppliesNothing.run();
      }
    });
  }
}
