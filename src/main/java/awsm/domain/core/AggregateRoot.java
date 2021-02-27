package awsm.domain.core;

public abstract class AggregateRoot<T> implements DomainEntity<T> {

  private transient DomainEvents events =
      DomainEvents.SpringManaged.INSTANCE.orElse(DomainEvents.Disabled.INSTANCE);

  protected void publish(DomainEvent event) {
    events.publish(event);
  }

  public void set(DomainEvents events) {
    this.events = events;
  }
}
