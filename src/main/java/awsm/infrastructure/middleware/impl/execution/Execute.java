package awsm.infrastructure.middleware.impl.execution;

import awsm.infrastructure.middleware.Command;
import awsm.infrastructure.middleware.Middleware;
import org.springframework.stereotype.Component;

@Component
public class Execute implements Middleware {

  private final Router router;

  public Execute(Router router) {
    this.router = router;
  }

  @Override
  public <R, C extends Command<R>> R invoke(C command, Next<R> next) {
    var executor = router.route(command);
    return executor.execute(command);
  }
}
