package awsm.domain.core;

import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
class SpringManagedDomainEvents implements DomainEvents {

    static Optional<DomainEvents> INSTANCE = Optional.empty();

    private final ListableBeanFactory beanFactory;

    SpringManagedDomainEvents(ListableBeanFactory beanFactory) {
        this.beanFactory = beanFactory;
        SpringManagedDomainEvents.INSTANCE = Optional.of(this);
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
