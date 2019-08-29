package awsm.infra.time;

import static java.time.Instant.EPOCH;
import static java.time.ZoneId.systemDefault;

import java.time.Clock;
import java.time.Duration;

public class TimeMachine {

  private static final ThreadLocal<Clock> clock = ThreadLocal.withInitial(Clock::systemUTC);

  public static Clock clock() {
    return clock.get();
  }

  public static void freezeEpoch() {
    var clock = Clock.fixed(EPOCH, systemDefault());
    TimeMachine.clock.set(clock);
  }

  public static void offset(Duration duration) {
    var offsetClock = Clock.offset(clock.get(), duration);
    TimeMachine.clock.set(offsetClock);
  }

}
