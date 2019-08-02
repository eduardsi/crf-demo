package lightweight4j.lib.pipeline.logging;

import an.awesome.pipelinr.Command;
import an.awesome.pipelinr.PipelineStep;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.function.Supplier;

@Component
@Order(1)
class Logging implements PipelineStep {

    private final Logger log = LoggerFactory.getLogger(Logging.class);

    private final CorrelationId correlationId;

    public Logging(CorrelationId correlationId) {
        this.correlationId = correlationId;
    }

    @Override
    public <R, C extends Command<R>> R invoke(C command, Next<R> next) {
        return correlationId.wrap(() -> {
            log.info(">>> {}", command.toString());
            var response = next.invoke();
            log.info("<<< {} ", response.toString());
            return response;
        });
    }

}
