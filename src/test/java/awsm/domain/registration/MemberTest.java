package awsm.domain.registration;

import static org.assertj.core.api.Assertions.assertThat;

import awsm.domain.DomainEvent;
import org.junit.jupiter.api.Test;

class MemberTest {

  private static final Email.Uniqueness uniqueness = email -> true;

  @Test
  void schedules_an_event_when_created() {

    var member = new Member(new Name("Uncle", "Bob"), new Email.Unique("uncle@domain.com", uniqueness));

    Registration event = (Registration) DomainEvent.lastPublished.get();
    assertThat(event.member()).isEqualTo(member);
  }


}
