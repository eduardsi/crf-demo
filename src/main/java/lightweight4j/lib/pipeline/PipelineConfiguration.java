package lightweight4j.lib.pipeline;

import an.awesome.pipelinr.Command;
import an.awesome.pipelinr.Pipeline;
import an.awesome.pipelinr.PipelineStep;
import an.awesome.pipelinr.Pipelinr;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class PipelineConfiguration {

    @Bean
    Pipeline pipeline(ObjectProvider<Command.Handler> commandHandlers, ObjectProvider<PipelineStep> pipelineSteps) {
        return new Pipelinr(commandHandlers::stream, pipelineSteps::orderedStream);
    }

}

