package lightweight4j.domain.registration;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MemberTest {

    @Test
    void schedules_an_event_when_created() {
        var MEMBER_ID = 123;
        var member = new Member(
                new Name("Uncle", "Bob"),
                new Email("uncle@domain.com"));
        member.id(MEMBER_ID);

        assertThat(member.events().stream().findFirst())
                .hasValueSatisfying(it -> {
                    RegistrationCompleted event = (RegistrationCompleted) it;
                    assertThat(event.memberId()).isEqualTo(MEMBER_ID);
                });
    }
}