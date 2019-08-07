package awsm.domain.registration;

import static java.util.Objects.requireNonNull;

import javax.persistence.Embeddable;

@Embeddable
public class Name {

  String firstOne;
  String lastOne;

  public Name(String firstOne, String lastOne) {
    this.firstOne = requireNonNull(firstOne, "First name cannot be null");
    this.lastOne = requireNonNull(lastOne, "Last name cannot be null");
  }

  private Name() {
  }

  @Override
  public String toString() {
    return firstOne + " " + lastOne;
  }
}
