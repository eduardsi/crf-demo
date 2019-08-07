package awsm.domain.registration;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class MemberTest {

  @Test
  void schedules_an_event_when_created() {
    var memberId = 123;
    var member = new Member(
            new Name("Uncle", "Bob"),
            new Email("uncle@domain.com"));
    member.id(memberId);

    assertThat(member.events().stream().findFirst())
            .hasValueSatisfying(it -> {
              RegistrationCompleted event = (RegistrationCompleted) it;
              assertThat(event.memberId()).isEqualTo(memberId);
            });
  }
}