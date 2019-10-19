package awsm.application.registration.impl;

import static java.util.Objects.requireNonNull;

class FullName {

  String firstName;

  String lastName;

  public FullName(String firstName, String lastName) {
    this.firstName = requireNonNull(firstName, "First name cannot be null");
    this.lastName = requireNonNull(lastName, "Last name cannot be null");
  }

  @Override
  public String toString() {
    return firstName + " " + lastName;
  }
}
