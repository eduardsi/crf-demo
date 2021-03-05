package awsm.infrastructure.scheduling;

import an.awesome.pipelinr.Command;
import org.springframework.stereotype.Component;

@Component
public class Scheduler {

  private final ScheduledCommandRepository repository;

  public Scheduler(ScheduledCommandRepository repository) {
    this.repository = repository;
  }

  public void schedule(Command command) {
    var scheduledCommand = new ScheduledCommand(command);
    repository.save(scheduledCommand);
  }
}
