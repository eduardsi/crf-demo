package awsm.infra.middleware.impl;

import awsm.infra.middleware.Command;
import awsm.infra.middleware.Middleware.Next.Null;
import awsm.infra.middleware.Middlewares;
import awsm.infra.middleware.impl.logging.Logging;
import awsm.infra.middleware.impl.react.React;
import awsm.infra.middleware.impl.resilience.Throttling;
import awsm.infra.middleware.impl.tx.Tx;
import awsm.infra.middleware.impl.validation.Validation;
import org.springframework.stereotype.Component;

@Component
class SpringMiddlewares implements Middlewares {

  private final React react;
  private final Tx tx;
  private final Validation validation;
  private final Logging logging;
  private final Throttling throttling;

  public SpringMiddlewares(React react, Tx tx, Validation validation, Logging logging, Throttling throttling) {
    this.react = react;
    this.tx = tx;
    this.validation = validation;
    this.logging = logging;
    this.throttling = throttling;
  }

  @Override
  public <R, C extends Command<R>> R send(C command) {
    return logging.invoke(command, () ->
                throttling.invoke(command, () ->
                      tx.invoke(command, () ->
                          validation.invoke(command, () ->
                                  react.invoke(command, new Null<>())))));
  }



}