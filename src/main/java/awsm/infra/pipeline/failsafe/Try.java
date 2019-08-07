package awsm.infra.pipeline.failsafe;

import static java.util.Collections.singletonList;

import an.awesome.pipelinr.Command;
import awsm.infra.pipeline.ExecutableCommand;
import net.jodah.failsafe.Failsafe;
import net.jodah.failsafe.RetryPolicy;
import org.springframework.stereotype.Component;

public class Try<R, C extends ExecutableCommand<R>> extends ExecutableCommand<R> {

  private final C origin;
  private final int attempts;

  public Try(int attempts, C origin) {
    this.origin = origin;
    this.attempts = attempts;
  }

  @Component
  static class Handler<R, C extends ExecutableCommand<R>> implements Command.Handler<Try<R, C>, R> {

    @Override
    public R handle(Try<R, C> tryCmd) {
      var origin = tryCmd.origin;
      var policy = new RetryPolicy<R>().withMaxAttempts(tryCmd.attempts);
      return Failsafe.with(singletonList(policy)).get(exec -> origin.execute());
    }
  }

}


