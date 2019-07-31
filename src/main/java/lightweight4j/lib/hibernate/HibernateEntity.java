package lightweight4j.lib.hibernate;

import javax.persistence.*;
import java.util.*;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;

@MappedSuperclass
public abstract class HibernateEntity {

    @Id
    @GeneratedValue
    private Long id;

    @Version
    private Long version;

    @Transient
    private transient final List<Object> events = new ArrayList<>();

    protected <T> void schedule(T event) {
        this.events.add(requireNonNull(event, "Event must not be null!"));
    }

    protected final void clearEvents() {
        this.events.clear();
    }

    protected final Stream<Object> events() {
        return Collections.unmodifiableCollection(events).stream();
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HibernateEntity that = (HibernateEntity) o;
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
