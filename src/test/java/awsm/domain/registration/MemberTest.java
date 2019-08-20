package awsm.domain.registration;

import static org.assertj.core.api.Assertions.assertThat;

import awsm.domain.DomainEvent;
import awsm.domain.registration.Email.NotBlacklisted;
import awsm.domain.registration.Email.Unique;
import org.junit.jupiter.api.Test;

class MemberTest {

  private static final Email.Uniqueness uniqueness = email -> true;
  private static final Email.Blacklist blacklist = email -> true;

  @Test
  void schedules_an_event_when_created() {

    var member = new Member(
        new Name("Uncle", "Bob"),
          new NotBlacklisted(blacklist,
              new Unique(uniqueness,
                  new Email("uncle@domain.com"))));

    RegistrationCompleted event = (RegistrationCompleted) DomainEvent.lastPublished.get();
    assertThat(event.member()).isEqualTo(member);
  }


}
