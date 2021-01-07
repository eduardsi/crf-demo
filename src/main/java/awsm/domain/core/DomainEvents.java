package awsm.domain.core;

public interface DomainEvents {

    void publish(DomainEvent event);

}
