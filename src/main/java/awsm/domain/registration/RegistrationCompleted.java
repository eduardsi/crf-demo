package awsm.domain.registration;

import awsm.domain.DomainEvent;

public class RegistrationCompleted implements DomainEvent {

  private final Member member;

  RegistrationCompleted(Member member) {
    this.member = member;
  }

  public Member member() {
    return member;
  }
}
