package awsm.domain.registration;

import static java.util.Objects.requireNonNull;

import awsm.infra.hibernate.HibernateConstructor;
import javax.persistence.Embeddable;

@Embeddable
public class FullName {

  private String firstName;
  private String lastName;

  public FullName(String firstName, String lastName) {
    this.firstName = requireNonNull(firstName, "First name cannot be null");
    this.lastName = requireNonNull(lastName, "Last name cannot be null");
  }

  @HibernateConstructor
  private FullName() {
  }

  @Override
  public String toString() {
    return firstName + " " + lastName;
  }
}
