package awsm.infrastructure.middleware.impl.resilience;

import awsm.infrastructure.middleware.Command;
import net.jodah.failsafe.Failsafe;
import net.jodah.failsafe.RetryPolicy;

public class Try<R, C extends Command<R>> implements Command<R> {

  private final C origin;

  public Try(C origin) {
    this.origin = origin;
  }

  @Override
  public R now() {
    var retry = new RetryPolicy<>().withMaxAttempts(3);
    return Failsafe.with(retry).get(origin::now);
  }

}
