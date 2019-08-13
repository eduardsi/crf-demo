package awsm.domain.registration;

import static org.assertj.core.api.Assertions.assertThat;

import awsm.domain.DomainEvent;
import org.junit.jupiter.api.Test;

class MemberTest {

  @Test
  void schedules_an_event_when_created() {
    var member = new Member(
            new Name("Uncle", "Bob"),
            new Email("uncle@domain.com"));

    Registration event = (Registration) DomainEvent.lastPublished.get();
    assertThat(event.member()).isEqualTo(member);
  }


}
