package awsm.infra.hibernate;

import static java.util.Collections.newSetFromMap;
import static java.util.Objects.requireNonNull;

import awsm.infra.modeling.Event;
import java.util.Collection;
import java.util.WeakHashMap;
import java.util.stream.Stream;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

public class Events {

  private final Collection<Event> events = newSetFromMap(new WeakHashMap<>());

  public Stream<Event> stream() {
    return events.stream();
  }

  public void schedule(Event event) {
    events.add(event);
    ApplicationEventPublisherHolder.get().publishEvent(event);
  }


  @Component
  static class ApplicationEventPublisherHolder {

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

  static class NullApplicationEventPublisher implements ApplicationEventPublisher {

    @Override
    public void publishEvent(Object event) {
      var logger = LoggerFactory.getLogger(ApplicationEventPublisherHolder.class);
      logger.warn("{} has not been published, because {} is used. Please set global {}.",
              event.getClass().getSimpleName(),
              NullApplicationEventPublisher.class.getSimpleName(),
              ApplicationEventPublisher.class.getSimpleName());
    }
  }


}
