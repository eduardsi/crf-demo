package lightweight4j.lib.hibernate;

import lightweight4j.lib.modeling.Entity;

import javax.annotation.Nullable;
import javax.persistence.*;
import java.util.*;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;

@MappedSuperclass
public abstract class HibernateEntity implements Entity {

    @Id
    @GeneratedValue
    @Nullable
    private Long id;

    @Version
    @Nullable
    private Long version;

    @Transient
    private transient final List<Object> events = new ArrayList<>();

    protected <T> void schedule(T event) {
        this.events.add(requireNonNull(event, "Event must not be null!"));
    }

    final void clearEvents() {
        this.events.clear();
    }

    final Stream<Object> events() {
        return Collections.unmodifiableCollection(events).stream();
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o instanceof HibernateEntity) {
            HibernateEntity that = (HibernateEntity) o;
            return Objects.equals(id, that.id);
        }
        return false;
    }

    @Override
    public final int hashCode() {
        return Objects.hash(id);
    }

    public Long id() {
        return requireNonNull(id, "ID is null. Perhaps the entity has not been persisted yet?");
    }
}
