package awsm.infra.middleware.impl.react;

import awsm.infra.middleware.Command;
import awsm.infra.middleware.Middleware;
import org.springframework.stereotype.Component;

@Component
public class React implements Middleware {

  private final Router router;

  public React(Router router) {
    this.router = router;
  }

  @Override
  public <R, C extends Command<R>> R invoke(C command, Next<R> next) {
    var reaction = router.route(command);
    var resilience = reaction.resilience();
    return resilience.wrapIfNecessary(() -> reaction.react(command));
  }
}
