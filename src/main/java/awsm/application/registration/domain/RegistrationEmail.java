package awsm.application.registration.domain;

import static com.google.common.base.Preconditions.checkArgument;

class RegistrationEmail extends Email {

  RegistrationEmail(String email, EmailUniqueness uniqueness, EmailBlacklist blacklist) {
    super(email);
    checkArgument(uniqueness.guaranteed(this), "Email %s is not unique", email);
    checkArgument(blacklist.allows(this), "Email %s is blacklisted", email);
  }

}
