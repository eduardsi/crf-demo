package awsm.application.registration.domain;

public class RegistrationCompleted {

  public final String fullName;
  public final String email;

  RegistrationCompleted(String fullName, String email) {
    this.fullName = fullName;
    this.email = email;
  }

}
