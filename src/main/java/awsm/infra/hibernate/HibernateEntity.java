package awsm.infra.hibernate;

import static java.util.Objects.requireNonNull;

import awsm.infra.modeling.Entity;
import java.util.Objects;
import javax.annotation.Nullable;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;
import javax.persistence.Version;

@MappedSuperclass
public abstract class HibernateEntity implements Entity {

  @Transient
  private final transient Events events = new Events();

  @Id
  @GeneratedValue
  @Nullable
  private Long id;

  @Version
  @Nullable
  private Long version;

  @Override
  public final boolean equals(Object o) {
    if (this == o) {
      return true;
    }
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

  public void id(long id) {
    this.id = id;
  }

  public Events events() {
    return events;
  }

}
