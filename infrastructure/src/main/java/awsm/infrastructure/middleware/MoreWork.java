package awsm.infrastructure.middleware;

import an.awesome.pipelinr.Command;
import awsm.infrastructure.middleware.scheduler.Scheduler;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class MoreWork {

  private final ApplicationEventPublisher publisher;
  private final Scheduler scheduler;

  public MoreWork(ApplicationEventPublisher publisher, Scheduler scheduler) {
    this.publisher = publisher;
    this.scheduler = scheduler;
  }

  public void outbox(Command command) {
    scheduler.schedule(command);
  }

  public void notify(Notification notification) {
    publisher.publishEvent(notification);
  }

}
