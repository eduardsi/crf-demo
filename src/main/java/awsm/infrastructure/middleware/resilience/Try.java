package awsm.infrastructure.middleware.resilience;

import an.awesome.pipelinr.Command;
import an.awesome.pipelinr.Pipeline;
import net.jodah.failsafe.Failsafe;
import net.jodah.failsafe.RetryPolicy;

public class Try<R, C extends Command<R>> implements Command<R> {

  private final C origin;

  public Try(C origin) {
    this.origin = origin;
  }

  @Override
  public R execute(Pipeline pipeline) {
    var retry = new RetryPolicy<>().withMaxAttempts(3);
    return Failsafe.with(retry).get(() -> origin.execute(pipeline));
  }

}
