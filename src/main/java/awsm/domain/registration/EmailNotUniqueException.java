package awsm.domain.registration;

import awsm.domain.DomainException;

public class EmailNotUniqueException extends DomainException {

  EmailNotUniqueException(Email email) {
    super("Email " + email + " is not unique");
  }
}
