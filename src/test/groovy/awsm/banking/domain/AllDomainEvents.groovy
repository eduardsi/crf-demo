package awsm.banking.domain

import awsm.banking.domain.core.DomainEvent
import awsm.banking.domain.core.DomainEvents
import com.google.common.collect.ForwardingCollection

class AllDomainEvents extends ForwardingCollection<DomainEvent> implements DomainEvents {

    private final Collection<DomainEvent> events = new ArrayList<>()

    @Override
    void publish(DomainEvent event) {
        events.add(event)
    }

    @Override
    protected Collection<DomainEvent> delegate() {
        return events
    }
}
