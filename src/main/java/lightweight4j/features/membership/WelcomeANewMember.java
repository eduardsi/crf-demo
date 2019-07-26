package lightweight4j.features.membership;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
class WelcomeANewMember {

    private static final Logger logger = LoggerFactory.getLogger(WelcomeANewMember.class);

    private final Members members;

    public WelcomeANewMember(Members members) {
        this.members = members;
    }

//    @Async
    @TransactionalEventListener
    public void when(MemberHasArrived $) {

        var member = members.findById($.memberId()).orElseThrow();

        logger.info("Greetings to {}", member.email());
    }

}
