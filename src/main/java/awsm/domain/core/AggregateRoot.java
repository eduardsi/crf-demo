package awsm.domain.core;

public abstract class AggregateRoot<T> implements DomainEntity<T>{

    private transient DomainEvents events = SpringManagedDomainEvents.INSTANCE.orElse(NoDomainEvents.INSTANCE);

    protected void publish(DomainEvent event) {
        events.publish(event);
    }

    public void set(DomainEvents events) {
        this.events = events;
    }

}
