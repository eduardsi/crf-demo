package awsm.domain.registration;

import awsm.domain.DomainEvent;

public class Registration implements DomainEvent {

  private final Member member;

  Registration(Member member) {
    this.member = member;
  }

  public Member member() {
    return member;
  }
}
