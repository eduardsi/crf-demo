package awsm.domain.core;

import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.stereotype.Component;

import java.util.Optional;


public interface DomainEvents {

    static DomainEvents defaultInstance() {
        return SpringManaged.INSTANCE.orElseGet(() -> new Unsupported());
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

    class Unsupported implements DomainEvents {
        @Override
        public void publish(DomainEvent event) {
            throw new UnsupportedOperationException("No domain events configured. Cannot send event: " + event);
        }
    }
}
