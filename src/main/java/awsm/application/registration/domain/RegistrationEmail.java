package awsm.application.registration.domain;

import static com.google.common.base.Preconditions.checkArgument;

class RegistrationEmail extends Email {

  RegistrationEmail(String email, EmailUniqueness uniqueness, EmailBlacklist blacklist) {
    super(email);
    checkArgument(uniqueness.guaranteed(email), "Email %s is not unique", email);
    checkArgument(blacklist.allows(email), "Email %s is blacklisted", email);
  }

}
