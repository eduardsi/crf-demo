package awsm.infra.middleware.impl.resilience;

import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import awsm.infra.middleware.Command;
import awsm.infra.middleware.impl.resilience.RateLimiter.ThrottlingException;
import awsm.util.concurrency.Threads;
import java.util.List;
import java.util.concurrent.CompletionException;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("throtting")
class ThrottlingTest {

  class Hello implements Command<String> {

    private AtomicLong executions = new AtomicLong();

    @Override
    public String execute() {
      try {
        Thread.sleep(1000);
      } catch (InterruptedException e) {
        //
      }
      executions.incrementAndGet();
      return "Hello";
    }
  }

  class AtATime implements RateLimit<Hello> {
    private final int max;

    AtATime(int max) {
      this.max = max;
    }

    @Override
    public int rateLimit() {
      return max;
    }
  }

  @Test
  void does_not_allow_more_than_max_executions_at_a_time() {
    var max = 2;
    var overflow = max + 2;
    var rateLimits = List.<RateLimit>of(new AtATime(max));
    var throttling = new Throttling(rateLimits);

    var hello = new Hello();
    var threads = new Threads(overflow);
    for (int i = 0; i < overflow; i++) {
      threads.spinOff(() -> throttling.invoke(hello, hello::execute));
    }

    var e = assertThrows(CompletionException.class, () -> threads.waitForAll());
    assertThat(e)
        .hasCauseInstanceOf(ThrottlingException.class)
        .hasMessageContaining(format("Reached the maximum number of permitted concurrent requests (%s)", max));
    assertThat(hello.executions).hasValue(max);
  }



}