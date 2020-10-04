package awsm.infrastructure.scheduling;

import an.awesome.pipelinr.Command;
import org.springframework.stereotype.Component;

@Component
public class Scheduler {

  private final ScheduledCommand.Repository repository;

  public Scheduler(ScheduledCommand.Repository repository) {
    this.repository = repository;
  }

  public void schedule(Command command) {
    var scheduledCommand = new ScheduledCommand(command);
    scheduledCommand.saveNew(repository);
  }

}
