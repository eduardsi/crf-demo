package awsm.infra.pipeline;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import an.awesome.pipelinr.Command;
import awsm.infra.pipeline.failsafe.Try;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Component;

@SpringBootTest
@Import(TryTest.Explode.Handler.class)
class TryTest {

  @Test
  void bubbles_up_an_exception_after_all_attempts() {
    var invocations = new AtomicLong();
    var attempts = 3;
    var explodeTimes = attempts;

    assertThrows(Explosion.class, () ->
            new Try<>(attempts,
                    new Explode(invocations, explodeTimes)).execute());
    assertThat(invocations.get()).isEqualTo(attempts);
  }

  @Test
  void completes_after_successful_retry_attempt() {
    var invocations = new AtomicLong();
    var attempts = 2;
    var explodeTimes = 1;

    var result = new Try<>(attempts,
            new Explode(invocations, explodeTimes)).execute();

    assertThat(result).isEqualTo("The dust has settled");
    assertThat(invocations.get()).isEqualTo(attempts);
  }

  static class Explode extends ExecutableCommand<String> {

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