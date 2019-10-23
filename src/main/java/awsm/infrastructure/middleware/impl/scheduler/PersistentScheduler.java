package awsm.infrastructure.middleware.impl.scheduler;

import awsm.infrastructure.middleware.Command;
import awsm.infrastructure.middleware.Scheduler;
import org.jooq.DSLContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;

@Component
class PersistentScheduler implements Scheduler {

  private final DSLContext dsl;

  public PersistentScheduler(DSLContext dsl) {
    this.dsl = dsl;
  }

  @Override
  public void schedule(Command command) {
    new ScheduledCommand(command).saveNew(dsl);
  }

  @Configuration
  @EnableScheduling
  static class SchedulerConfig {
  }

}
