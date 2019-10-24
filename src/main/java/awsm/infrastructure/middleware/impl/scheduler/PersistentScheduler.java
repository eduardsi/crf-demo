package awsm.infrastructure.middleware.impl.scheduler;

import awsm.infrastructure.middleware.Command;
import awsm.infrastructure.middleware.Scheduler;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;

@Component
class PersistentScheduler implements Scheduler {

  private final ScheduledCommand.Repository repository;

  public PersistentScheduler(ScheduledCommand.Repository repository) {
    this.repository = repository;
  }

  @Override
  public void schedule(Command command) {
    var scheduledCommand = new ScheduledCommand(command);
    repository.insert(scheduledCommand);
  }

  @Configuration
  @EnableScheduling
  static class SchedulerConfig {
  }

}
