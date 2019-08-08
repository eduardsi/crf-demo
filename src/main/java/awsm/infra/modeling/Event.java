package awsm.infra.modeling;

import static java.util.Objects.requireNonNull;

import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

public interface Event {

  ThreadLocal<Event> lastPublished = new ThreadLocal<>();

  default void schedule() {
    ApplicationEventPublisherHolder.get().publishEvent(this);
    lastPublished.set(this);
  }

  @Component
  class ApplicationEventPublisherHolder {

    private static ApplicationEventPublisher INSTANCE = new NullApplicationEventPublisher();

    public ApplicationEventPublisherHolder(ApplicationEventPublisher publisher) {
      set(publisher);
    }

    static void set(ApplicationEventPublisher publisher) {
      ApplicationEventPublisherHolder.INSTANCE = requireNonNull(publisher, "Publisher cannot be null");
    }

    static ApplicationEventPublisher get() {
      return INSTANCE;
    }

  }

  class NullApplicationEventPublisher implements ApplicationEventPublisher {

    @Override
    public void publishEvent(Object event) {
      var logger = LoggerFactory.getLogger(ApplicationEventPublisherHolder.class);
      logger.warn("{} has not been published, because {} is used. For production usage, set global {}.",
              event.getClass().getSimpleName(),
              NullApplicationEventPublisher.class.getSimpleName(),
              ApplicationEventPublisher.class.getSimpleName());
    }
  }

}
