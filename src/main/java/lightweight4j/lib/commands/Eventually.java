package lightweight4j.lib.commands;

import org.springframework.stereotype.Component;

@Component
public class Eventually {

    public <R> void execute(Command<R> command) {
        // enqueue in ActiveMQ
    }
}

