package awsm.domain.registration;

import awsm.domain.DomainException;

public class EmailBlacklistedException extends DomainException {

  private Email email;

  public EmailBlacklistedException(Email email) {

    this.email = email;
  }

  @Override
  public String getMessage() {
    return "Email " + email + " is blacklisted";
  }
}
