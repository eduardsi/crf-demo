package lightweight4j.domain.registration;

import lightweight4j.infra.modeling.Event;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.context.ApplicationEventPublisher;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

class MemberTest {

    @Mock
    ApplicationEventPublisher eventPublisher;

    @Captor
    ArgumentCaptor<RegistrationCompleted> captor;

    @BeforeEach
    void beforeEach() {
        MockitoAnnotations.initMocks(this);
        Event.Publisher.setThreadLocal(eventPublisher);
    }

    @Test
    void publishes_an_event_when_created() {
        // when new member is created
        var MEMBER_ID = 123;
        var member = new Member(
                new Name("Uncle", "Bob"),
                new Email("uncle@domain.com"));
        member.id(MEMBER_ID);

        // then event must be published
        verify(eventPublisher).publishEvent(captor.capture());
        var event = captor.getValue();

        // and event must provide some attributes of a member
        assertThat(event.memberId()).isEqualTo(MEMBER_ID);
    }
}