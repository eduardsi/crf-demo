package lightweight4j.lib.pipeline.logging;

import an.awesome.pipelinr.Command;
import an.awesome.pipelinr.PipelineStep;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(1)
class Correlation implements PipelineStep {

    private final CorrelationId correlationId;

    @Autowired
    Correlation(CorrelationId correlationId) {
        this.correlationId = correlationId;
    }

    @Override
    public <R, C extends Command<R>> R invoke(C command, Next<R> next) {
        try (var stashAutomatically = correlationId.storeForLogging()) {
            return next.invoke();
        }
    }
}
