package awsm.domain.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.stereotype.Component;

import java.util.Optional;

public interface DomainEvents {

    DomainEvents NONE = new NoDomainEvents();

    Logger logger = LoggerFactory.getLogger(DomainEvents.class);

    static DomainEvents defaultInstance() {
        return SpringManaged.INSTANCE.orElse(NONE);
    }

    void publish(DomainEvent event);

    @Component
    class SpringManaged implements DomainEvents {

        private static Optional<DomainEvents> INSTANCE = Optional.empty();

        private final ListableBeanFactory beanFactory;

        SpringManaged(ListableBeanFactory beanFactory) {
            this.beanFactory = beanFactory;
            SpringManaged.INSTANCE = Optional.of(this);
        }

        @SuppressWarnings({"unchecked", "UnstableApiUsage"})
        @Override
        public void publish(DomainEvent event) {
            beanFactory
                    .getBeansOfType(DomainEvent.SideEffect.class)
                    .values()
                    .stream()
                    .filter(sideEffect -> sideEffect.eventType().isSupertypeOf(event.type()))
                    .forEach(sideEffect -> sideEffect.trigger(event));
        }

    }

    class NoDomainEvents implements DomainEvents {
        @Override
        public void publish(DomainEvent event) {
            logger.warn("No domain events configured. Cannot send event: " + event);
        }
    }
}
