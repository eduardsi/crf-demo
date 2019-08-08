package awsm.domain.registration;

import static java.util.Objects.requireNonNull;

import awsm.infra.hibernate.HibernateConstructor;
import javax.persistence.Embeddable;

@Embeddable
public class Name {

  private String firstOne;
  private String lastOne;

  public Name(String firstOne, String lastOne) {
    this.firstOne = requireNonNull(firstOne, "First name cannot be null");
    this.lastOne = requireNonNull(lastOne, "Last name cannot be null");
  }

  @HibernateConstructor
  private Name() {
  }

  @Override
  public String toString() {
    return firstOne + " " + lastOne;
  }
}
