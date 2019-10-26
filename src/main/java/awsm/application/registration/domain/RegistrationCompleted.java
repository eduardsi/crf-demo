package awsm.application.registration.domain;

import awsm.infrastructure.modeling.DomainEvent;

public class RegistrationCompleted implements DomainEvent {

  public final String fullName;
  public final String email;

  RegistrationCompleted(String fullName, String email) {
    this.fullName = fullName;
    this.email = email;
  }

}
