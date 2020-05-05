package awsm.infrastructure.middleware.scheduler;

import an.awesome.pipelinr.Command;
import org.springframework.stereotype.Component;

@Component
public class Outbox {

  private final ScheduledCommand.Repository repository;

  public Outbox(ScheduledCommand.Repository repository) {
    this.repository = repository;
  }

  public void put(Command command) {
    var scheduledCommand = new ScheduledCommand(command);
    scheduledCommand.saveNew(repository);
  }

}
