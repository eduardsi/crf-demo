package awsm.infra.pipeline.logging;

import an.awesome.pipelinr.Command;
import an.awesome.pipelinr.PipelineStep;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

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

}
