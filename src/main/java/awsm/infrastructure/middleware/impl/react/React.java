package awsm.infrastructure.middleware.impl.react;

import awsm.infrastructure.middleware.Command;
import awsm.infrastructure.middleware.Middleware;
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
    return reaction.react(command);
  }
}
