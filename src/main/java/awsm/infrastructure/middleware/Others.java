package awsm.infrastructure.middleware;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class Others {

  private final ApplicationEventPublisher publisher;

  public Others(ApplicationEventPublisher publisher) {
    this.publisher = publisher;
  }

  public void notify(DomainEvent domainEvent) {
    publisher.publishEvent(domainEvent);
  }

}
