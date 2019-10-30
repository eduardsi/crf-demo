package awsm.crm;

import awsm.infrastructure.middleware.Notification;

public class RegistrationCompleted implements Notification {

  public final String fullName;
  public final String email;

  RegistrationCompleted(String fullName, String email) {
    this.fullName = fullName;
    this.email = email;
  }

}
