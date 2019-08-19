package awsm.domain.registration;

import awsm.domain.DomainException;

public class EmailBlacklistedException extends DomainException {

  public EmailBlacklistedException(Email email) {
    super("Email " + email + " is blacklisted");
  }

}
