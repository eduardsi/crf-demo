package lightweight4j.lib.domain;


import org.springframework.data.domain.AfterDomainEventPublication;
import org.springframework.data.domain.DomainEvents;
import org.springframework.util.Assert;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;
import java.util.*;

import static java.util.Objects.requireNonNull;

@MappedSuperclass
public abstract class DomainEntity {

    @Id
    @GeneratedValue
    private Long id;

    @Transient
    private transient final List<DomainEvent> domainEvents = new ArrayList<>();

    protected <T extends DomainEvent> void schedule(T event) {
        this.domainEvents.add(requireNonNull(event, "Domain event must not be null!"));
    }

    @AfterDomainEventPublication
    protected void clearEvents() {
        this.domainEvents.clear();
    }

    @DomainEvents
    protected Collection<DomainEvent> events() {
        return Collections.unmodifiableList(domainEvents);
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DomainEntity that = (DomainEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public final int hashCode() {
        return Objects.hash(id);
    }

    public Long id() {
        return id;
    }
}
