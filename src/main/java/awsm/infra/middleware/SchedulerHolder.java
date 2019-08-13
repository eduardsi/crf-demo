package awsm.infra.middleware;

import static java.util.Objects.requireNonNull;

import org.springframework.stereotype.Component;

@Component
class SchedulerHolder {

  private static Scheduler INSTANCE = new NoScheduler();

  public SchedulerHolder(Scheduler scheduler) {
    set(scheduler);
  }

  static void set(Scheduler scheduler) {
    SchedulerHolder.INSTANCE = requireNonNull(scheduler, "Scheduler cannot be null");
  }

  static Scheduler get() {
    return INSTANCE;
  }

}
