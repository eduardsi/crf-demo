package awsm.infra.pipeline.resilience;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import an.awesome.pipelinr.Command;
import an.awesome.pipelinr.Pipeline;
import an.awesome.pipelinr.Voidy;
import awsm.infra.pipeline.ExecutableCommand;
import java.time.Duration;
import java.util.concurrent.TimeoutException;
import net.jodah.failsafe.FailsafeException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Component;

@SpringBootTest
@Import(ResilienceTest.SlowCommand.Handler.class)
class ResilienceTest {

  private static final long MAX_DURATION = 100;

  @Autowired
  Pipeline pipeline;

  @Test
  void doesNotTimeOutIfCommandCompletesBeforeMaxDuration() {
    assertDoesNotThrow(() -> {
      pipeline.send(new SlowCommand(MAX_DURATION - 50, MAX_DURATION));
    });
  }

  @Test
  void timeOutsIfCommandRunsLongerThanMaxDuration() {
    var e = assertThrows(FailsafeException.class, () ->
            pipeline.send(new SlowCommand(MAX_DURATION, MAX_DURATION)));
    assertThat(e).hasCauseInstanceOf(TimeoutException.class);
  }

  static class SlowCommand extends ExecutableCommand<Voidy> implements Timeoutable {

    private final Duration sleep;
    private final Duration maxDuration;

    SlowCommand(long sleep, long maxDuration) {
      this.sleep = Duration.ofMillis(sleep);
      this.maxDuration = Duration.ofMillis(maxDuration);
    }

    @Override
    public Duration maxDuration() {
      return maxDuration;
    }

    @Component
    static class Handler implements Command.Handler<SlowCommand, Voidy> {

      @Override
      public Voidy handle(SlowCommand command) {
        try {
          Thread.sleep(command.sleep.toMillis());
        } catch (InterruptedException e) {
          throw new RuntimeException(e);
        }
        return new Voidy();
      }
    }
  }



}