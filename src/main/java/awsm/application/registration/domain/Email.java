package awsm.application.registration.domain;

import static com.google.common.base.Preconditions.checkArgument;

class Email {

  private final String email;

  Email(String email, EmailUniqueness uniqueness, EmailBlacklist blacklist) {
    var isNotBlank = email != null && !email.isBlank();
    checkArgument(isNotBlank, "Email %s must not be blank", email);
    checkArgument(uniqueness.guaranteed(email), "Email %s is not unique", email);
    checkArgument(blacklist.allows(email), "Email %s is blacklisted", email);
    this.email = email;
  }

  Email(String email) {
    this.email = email;
  }

  @Override
  public final String toString() {
    return email;
  }

}
