package lightweight4j.lib.pipeline;

import an.awesome.pipelinr.Command;
import an.awesome.pipelinr.Pipeline;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;
import javax.validation.constraints.Null;

import static java.util.Objects.requireNonNull;

public interface ExecutableCommand<T> extends Command<T> {

    default T execute() {
        return execute(Injected.pipeline);
    }

    default T execute(Pipeline pipeline) {
        return requireNonNull(pipeline, "Pipeline cannot be null").send(this);
    }

    @Component
    class Injected {

        @SuppressWarnings("NullAway.Init")
        private static Pipeline pipeline;

        public Injected(Pipeline pipeline) {
            Injected.pipeline = pipeline;
        }
    }

}
