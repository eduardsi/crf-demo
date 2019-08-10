package awsm.infra.middleware.impl.react;

import awsm.infra.middleware.Command;
import org.springframework.stereotype.Component;

@Component
class Router {

  private final Reactions reactions;

  public Router(Reactions reactions) {
    this.reactions = reactions;
  }

  @SuppressWarnings("unchecked")
  <R, C extends Command<R>> Reaction<C, R> route(C command) {
    var commandName = command.getClass().getSimpleName();
    var reaction = reactions
            .stream()
            .filter(it -> it.matches(command))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Cannot find reaction for " + commandName));
    return reaction;
  }


}
