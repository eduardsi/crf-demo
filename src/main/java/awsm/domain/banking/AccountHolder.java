package awsm.domain.banking;

import javax.persistence.Embeddable;

@Embeddable
public class AccountHolder {
  private String email;
  private String personalId;
  private String firstName;
  private String lastName;

  public AccountHolder(String firstName, String lastName, String personalId, String email) {
    this.firstName = firstName;
    this.lastName = lastName;
    this.personalId = personalId;
    this.email = email;
  }

  private AccountHolder() {}

  public String email() {
    return email;
  }

  public String name() {
    return firstName + " " + lastName;
  }
}
