package lightweight4j.lib.commands;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class Loggable implements PipelineBehavior {

    private final Logger log = LoggerFactory.getLogger(Loggable.class);

    private final PipelineBehavior origin;

    public Loggable(PipelineBehavior origin) {
        this.origin = origin;
    }

    @Override
    public <R, C extends Command<R>> R mixIn(C command) {
        log.info(">>> {}", command.toString());
        var response = origin.mixIn(command);
        log.info("<<< {} ", response.toString());
        return response;
    }
}