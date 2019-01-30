package lightweight4j.lib.commands;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class Loggable implements Now {

    private final Logger log = LoggerFactory.getLogger(Loggable.class);

    private final Now origin;

    public Loggable(Now origin) {
        this.origin = origin;
    }

    @Override
    public <R, C extends Command<R>> R execute(C command) {
        log.info(">>> {}", command.toString());
        var response = origin.execute(command);
        log.info("<<< {} ", response.toString());
        return response;
    }
}