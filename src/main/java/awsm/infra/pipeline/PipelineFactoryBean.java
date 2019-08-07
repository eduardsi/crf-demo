package awsm.infra.pipeline;

import an.awesome.pipelinr.Command;
import an.awesome.pipelinr.Pipeline;
import an.awesome.pipelinr.PipelineStep;
import an.awesome.pipelinr.Pipelinr;
import javax.annotation.Nonnull;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.config.AbstractFactoryBean;
import org.springframework.stereotype.Component;

@Component
class PipelineFactoryBean extends AbstractFactoryBean<Pipeline> {

  private ObjectProvider<Command.Handler> commandHandlers;
  private ObjectProvider<PipelineStep> pipelineSteps;

  public PipelineFactoryBean(ObjectProvider<Command.Handler> commandHandlers, ObjectProvider<PipelineStep> pipelineSteps) {
    this.commandHandlers = commandHandlers;
    this.pipelineSteps = pipelineSteps;
  }

  @Override
  public Class<?> getObjectType() {
    return Pipeline.class;
  }

  @Override
  @Nonnull
  protected Pipeline createInstance() {
    return new Pipelinr(commandHandlers::stream, pipelineSteps::orderedStream);
  }
}

