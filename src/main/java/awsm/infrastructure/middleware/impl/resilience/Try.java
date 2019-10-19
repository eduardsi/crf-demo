package awsm.infrastructure.middleware.impl.resilience;

import awsm.infrastructure.middleware.Command;
import awsm.infrastructure.middleware.impl.react.Reaction;
import net.jodah.failsafe.Failsafe;
import net.jodah.failsafe.RetryPolicy;
import org.springframework.stereotype.Component;

public class Try<R, C extends Command<R>> implements Command<R> {

  private final C origin;

  public Try(C origin) {
    this.origin = origin;
  }

  @Component
  static class Re<R, C extends Command<R>> implements Reaction<Try<R, C>, R> {
    @Override
    public R react(Try<R, C> cmd) {
      var retry = new RetryPolicy<>().withMaxAttempts(3);
      return Failsafe.with(retry).get(cmd.origin::execute);
    }
  }

}
