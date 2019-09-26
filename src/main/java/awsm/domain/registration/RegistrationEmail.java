package awsm.domain.registration;

public class RegistrationEmail extends Email {

  public RegistrationEmail(Email email, Uniqueness uniqueness, Blacklist blacklist) {
    super(email);
    new Unique(uniqueness, email);
    new NotBlacklisted(blacklist, email);
  }


}
