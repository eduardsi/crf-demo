package awsm.banking.domain.core;

public abstract class AggregateRoot<T> implements DomainEntity<T>{

    private transient DomainEvents events = DomainEvents.defaultInstance();

    protected void publish(DomainEvent event) {
        events.publish(event);
    }

    public void set(DomainEvents events) {
        this.events = events;
    }

}
