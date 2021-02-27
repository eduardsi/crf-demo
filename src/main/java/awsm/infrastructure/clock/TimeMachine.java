package awsm.infrastructure.clock;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class TimeMachine {

  private static final ThreadLocal<Clock> clock = ThreadLocal.withInitial(Clock::systemUTC);

  private static Clock clock() {
    return clock.get();
  }

  public static void set(Clock clock) {
    TimeMachine.clock.set(clock);
  }

  public static LocalDate today() {
    return LocalDate.now(clock());
  }

  public static LocalDateTime now() {
    return LocalDateTime.now(clock());
  }
}
