package awsm.domain.offers;

import static awsm.domain.offers.Offer.Status.PENDING;
import static java.util.Objects.requireNonNull;

import awsm.infra.hibernate.HibernateConstructor;
import com.google.common.base.Preconditions;
import javax.annotation.Nullable;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Version;

@Entity
public class Offer {

  enum Status {
    PENDING, ACCEPTED
  }

  @Id
  @Nullable
  @GeneratedValue
  private Long id;

  @Enumerated(EnumType.STRING)
  private Status status = PENDING;

  private $ price;

  @Version
  private long version;

  public Offer($ price) {
    this.price = price;
  }

  @HibernateConstructor
  private Offer() {
  }

  public void raiseBy($ margin) {
    this.price = this.price.add(margin);
  }

  public void accept() {
    Preconditions.checkState(status == PENDING, "Cannot accept %s offer. Must be %s.", status, PENDING);
    this.status = Status.ACCEPTED;
  }

  public $ price() {
    return price;
  }

  public Long id() {
    return requireNonNull(id, "ID is null. Perhaps the entity has not been persisted yet?");
  }

}
