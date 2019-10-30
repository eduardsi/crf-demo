package awsm.crm;

import static com.google.common.base.Preconditions.checkArgument;

class Email {

  private final String email;

  Email(String email) {
    var isNotBlank = email != null && !email.isBlank();
    checkArgument(isNotBlank, "Email %s must not be blank", email);
    this.email = email;
  }

  @Override
  public final String toString() {
    return email;
  }

}
