package awsm.domain.core;

import com.google.common.collect.ForwardingCollection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;

public interface DomainEvents {

  DomainEvents INSTANCE =
      DomainEvents.SpringManaged.INSTANCE.orElse(DomainEvents.Disabled.INSTANCE);

  void publish(DomainEvent event);

  class Disabled extends ForwardingCollection<DomainEvent> implements DomainEvents {
    private static final Logger logger = LoggerFactory.getLogger(Disabled.class);
    static Disabled INSTANCE = new Disabled();

    private final Collection<DomainEvent> events = new ArrayList<>();

    @Override
    public void publish(DomainEvent event) {
      logger.warn("No domain events configured. Cannot send event: " + event);
      events.add(event);
    }

    @Override
    protected Collection<DomainEvent> delegate() {
      return events;
    }
  }

  @SuppressWarnings({"rawtypes", "unchecked", "UnstableApiUsage"})
  @Component
  class SpringManaged implements DomainEvents {
    static Optional<DomainEvents> INSTANCE = Optional.empty();
    private final ObjectProvider<DomainEvent.SideEffect> sideEffects;

    SpringManaged(ObjectProvider<DomainEvent.SideEffect> sideEffects) {
      this.sideEffects = sideEffects;
      SpringManaged.INSTANCE = Optional.of(this);
    }

    @Override
    public void publish(DomainEvent event) {
      sideEffects.stream()
          .filter(sideEffect -> sideEffect.eventType().isSupertypeOf(event.type()))
          .forEach(sideEffect -> sideEffect.trigger(event));
    }
  }
}
