package lightweight4j.infra.pipeline;

import an.awesome.pipelinr.Command;
import an.awesome.pipelinr.Pipeline;
import lightweight4j.infra.modeling.Data;
import org.springframework.stereotype.Component;

import static java.util.Objects.requireNonNull;

public abstract class ExecutableCommand<T> extends Data implements Command<T> {

    public final T execute() {
        return execute(Injected.pipeline);
    }

    public final T execute(Pipeline pipeline) {
        return requireNonNull(pipeline, "Pipeline cannot be null").send(this);
    }

    @Component
    static class Injected {

        @SuppressWarnings("NullAway.Init")
        private static Pipeline pipeline;

        public Injected(Pipeline pipeline) {
            Injected.pipeline = pipeline;
        }
    }

}
