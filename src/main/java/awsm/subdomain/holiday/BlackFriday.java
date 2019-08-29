package awsm.subdomain.holiday;

import static awsm.infra.time.TimeMachine.clock;
import static java.time.DayOfWeek.THURSDAY;
import static java.time.temporal.TemporalAdjusters.lastInMonth;

import java.time.LocalDate;
import java.time.Month;

public class BlackFriday {

  public boolean fallsOn(LocalDate date) {
    var blackFriday = thanksgiving().plusDays(1);
    return blackFriday.isEqual(date);
  }

  private LocalDate thanksgiving() {
    return LocalDate.now(clock())
        .with(Month.NOVEMBER)
        .with(lastInMonth(THURSDAY));
  }

}
