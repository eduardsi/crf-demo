package lightweight4j.lib.pipeline;

import an.awesome.pipelinr.Command;
import net.jodah.failsafe.Failsafe;
import net.jodah.failsafe.RetryPolicy;
import org.springframework.stereotype.Component;

import static java.util.Collections.singletonList;

public class Try<R, C extends ExecutableCommand<R>> implements ExecutableCommand<R> {

    private final C origin;
    private final int attempts;

    public Try(int attempts, C origin) {
        this.origin = origin;
        this.attempts = attempts;
    }

    @Component
    static class Handler<R, C extends ExecutableCommand<R>> implements Command.Handler<Try<R, C>, R> {

        @Override
        public R handle(Try<R, C> command) {
            var origin = command.origin;
            var policy = new RetryPolicy<R>().withMaxAttempts(command.attempts);
            return Failsafe.with(singletonList(policy)).get(exec -> origin.execute());
        }
    }

}


