package awsm.domain.registration;

import awsm.domain.DomainEvent;

public class NewMemberEvent implements DomainEvent {

  private final Member member;

  NewMemberEvent(Member member) {
    this.member = member;
  }

  public Member member() {
    return member;
  }
}
