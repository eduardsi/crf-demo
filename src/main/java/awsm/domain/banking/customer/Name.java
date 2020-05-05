package awsm.domain.banking.customer;

import static java.util.Objects.requireNonNull;

public class Name {

  public String firstName;

  public String lastName;

  public Name(String firstName, String lastName) {
    this.firstName = requireNonNull(firstName, "First name cannot be null");
    this.lastName = requireNonNull(lastName, "Last name cannot be null");
  }

  @Override
  public String toString() {
    return firstName + " " + lastName;
  }
}
