package lightweight4j.lib.commands;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class Logging implements Now.Filter {

    private final Logger log = LoggerFactory.getLogger(Logging.class);

    private final Now.Filter origin;

    public Logging(Now.Filter origin) {
        this.origin = origin;
    }

    @Override
    public <R, C extends Command<R>> R process(C command) {
        log.info(">>> {}", command.toString());
        var response = origin.process(command);
        log.info("<<< {} ", response.toString());
        return response;
    }
}