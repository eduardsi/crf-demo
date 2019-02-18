package lightweight4j.lib.pipeline.logging;

import an.awesome.pipelinr.Command;
import an.awesome.pipelinr.PipelineStep;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(2)
class CommandLogging implements PipelineStep {

    private final Logger log = LoggerFactory.getLogger(CommandLogging.class);

    @Override
    public <R, C extends Command<R>> R invoke(C command, Next<R> next) {
        log.info(">>> {}", command.toString());
        var response = next.invoke();
        log.info("<<< {} ", response.toString());
        return response;
    }

}
