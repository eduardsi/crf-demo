package awsm.infra.middleware.impl.scheduler;

import static awsm.infra.middleware.impl.scheduler.ScheduledCommand.Status.PENDING;

import awsm.infra.middleware.Command;
import awsm.infra.middleware.Scheduler;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
class PersistentScheduler implements Scheduler {

  private static final int MAX_ATTEMPTS = 3;

  private final ScheduledCommands scheduledCommands;

  public PersistentScheduler(ScheduledCommands scheduledCommands) {
    this.scheduledCommands = scheduledCommands;
  }

  @Override
  public void schedule(Command command) {
    scheduledCommands.save(new ScheduledCommand(command));
  }

  @Transactional(noRollbackFor = Exception.class)
  @Scheduled(initialDelay = 5000, fixedDelay = 1000)
  public void executeOldest() {
    scheduledCommands.findFirstByTouchedTimesLessThanAndStatus(MAX_ATTEMPTS, PENDING).ifPresent(ScheduledCommand::execute);
  }

}
