package awsm.domain.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NoDomainEvents implements DomainEvents {

    private static final Logger logger = LoggerFactory.getLogger(NoDomainEvents.class);

    static NoDomainEvents INSTANCE = new NoDomainEvents();

    @Override
    public void publish(DomainEvent event) {
        logger.warn("No domain events configured. Cannot send event: " + event);
    }
}
