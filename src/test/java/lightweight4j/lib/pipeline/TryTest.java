package lightweight4j.lib.pipeline;

import an.awesome.pipelinr.Command;
import lightweight4j.lib.pipeline.failsafe.Try;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicLong;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Import(TryTest.Explode.Handler.class)
class TryTest {

    @Test
    void bubblesExceptionUpAfterAllAttempts() {
        var invocations = new AtomicLong();
        var attempts = 3;
        var explodeTimes = attempts;

        assertThrows(Explosion.class, () ->
                new Try<>(attempts,
                    new Explode(invocations, explodeTimes)).execute());

        assertThat(invocations.get()).isEqualTo(attempts);
    }

    @Test
    void completesAfterSuccessfulRetryAttempt() {
        var invocations = new AtomicLong();
        var attempts = 2;
        var explodeTimes = 1;

        var result = new Try<>(attempts,
                new Explode(invocations, explodeTimes)).execute();

        assertThat(result).isEqualTo("The dust has settled");
        assertThat(invocations.get()).isEqualTo(attempts);
    }

    static class Explode implements ExecutableCommand<String> {

        final AtomicLong invocations;
        final int times;

        Explode(AtomicLong invocations, int times) {
            this.invocations = invocations;
            this.times = times;
        }

        @Component
        static class Handler implements Command.Handler<TryTest.Explode, String> {

            @Override
            public String handle(TryTest.Explode command) {

                long attempt = command.invocations.incrementAndGet();
                long times = command.times;
                if (attempt <= times) {
                    throw new TryTest.Explosion();
                } else {
                    return "The dust has settled";
                }
            }
        }

    }

    private static class Explosion extends RuntimeException {

    }
}