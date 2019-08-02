package lightweight4j.lib.pipeline.logging;

import an.awesome.pipelinr.Command;
import an.awesome.pipelinr.PipelineStep;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.function.Supplier;

import static org.apache.commons.lang3.builder.ToStringBuilder.reflectionToString;
import static org.apache.commons.lang3.builder.ToStringStyle.NO_CLASS_NAME_STYLE;

@Component
@Order(1)
class Logging implements PipelineStep {

    private final CorrelationId correlationId;

    public Logging(CorrelationId correlationId) {
        this.correlationId = correlationId;
    }

    @Override
    public <R, C extends Command<R>> R invoke(C command, Next<R> next) {
        var logger = logger(command);
        return correlationId.wrap(() -> {
            logger.info(">>> {}", command);
            var response = next.invoke();
            logger.info("<<< {}", response);
            return response;
        });
    }

    private <R, C extends Command<R>> Logger logger(C command) {
        return LoggerFactory.getLogger(command.getClass());
    }

    private void log(Logger logger, Object object) {
        logger.info(reflectionToString(object, NO_CLASS_NAME_STYLE));
    }

}
