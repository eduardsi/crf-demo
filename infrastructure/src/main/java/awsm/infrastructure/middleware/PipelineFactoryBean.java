package awsm.infrastructure.middleware;

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

  private final ObjectProvider<Command.Handler> handlers;
  private final ObjectProvider<PipelineStep> steps;

  PipelineFactoryBean(ObjectProvider<Command.Handler> handlers, ObjectProvider<PipelineStep> steps) {
    this.handlers = handlers;
    this.steps = steps;
  }

  @Override
  public Class<?> getObjectType() {
    return Pipeline.class;
  }

  @Override
  @Nonnull
  protected Pipeline createInstance() {
    return new Pipelinr(handlers::stream, steps::orderedStream);
  }
}
