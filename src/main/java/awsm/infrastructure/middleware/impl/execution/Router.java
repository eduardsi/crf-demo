package awsm.infrastructure.middleware.impl.execution;

import awsm.infrastructure.middleware.Command;
import org.springframework.stereotype.Component;

@Component
class Router {

  private final Executors executors;

  public Router(Executors executors) {
    this.executors = executors;
  }

  @SuppressWarnings("unchecked")
  <R, C extends Command<R>> Executor<C, R> route(C command) {
    var commandName = command.getClass().getSimpleName();
    var executor = executors
            .stream()
            .filter(it -> it.matches(command))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Cannot find executor for " + commandName + " command"));
    return executor;
  }


}
