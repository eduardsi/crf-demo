package awsm.infrastructure.middleware.impl;

import awsm.infrastructure.middleware.Command;
import awsm.infrastructure.middleware.Middleware.Next.Null;
import awsm.infrastructure.middleware.Middlewares;
import awsm.infrastructure.middleware.impl.logging.Logging;
import awsm.infrastructure.middleware.impl.execution.Execute;
import awsm.infrastructure.middleware.impl.resilience.Throttling;
import awsm.infrastructure.middleware.impl.tx.Tx;
import org.springframework.stereotype.Component;

@Component
class SpringMiddlewares implements Middlewares {

  private final Execute executor;
  private final Tx tx;
  private final Logging logging;
  private final Throttling throttling;

  public SpringMiddlewares(Execute executor, Tx tx, Logging logging, Throttling throttling) {
    this.executor = executor;
    this.tx = tx;
    this.logging = logging;
    this.throttling = throttling;
  }

  @Override
  public <R, C extends Command<R>> R send(C command) {
    return logging.invoke(command, () ->
                throttling.invoke(command, () ->
                      tx.invoke(command, () ->
                          executor.invoke(command, new Null<>()))));
  }



}
