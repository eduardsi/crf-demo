package lightweight4j.features.registration.impl;

import an.awesome.pipelinr.Command;
import an.awesome.pipelinr.Voidy;
import lightweight4j.features.registration.Greeting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
class GreetingHandler implements Command.Handler<Greeting, Voidy> {

    private static final Logger logger = LoggerFactory.getLogger(GreetingHandler.class);

    private final Members members;

    public GreetingHandler(Members members) {
        this.members = members;
    }

    @Override
    public Voidy handle(Greeting command) {
        var member = members.findById(command.memberId).orElseThrow();
        logger.info("Greetings to {}", member.email);
        return new Voidy();
    }
}
