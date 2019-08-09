package awsm.infra.pipeline.resilience;

import an.awesome.pipelinr.Command;
import an.awesome.pipelinr.PipelineStep;
import net.jodah.failsafe.Failsafe;
import net.jodah.failsafe.Timeout;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(1)
class Resilience implements PipelineStep {

  @Override
  public <R, C extends Command<R>> R invoke(C command, Next<R> next) {

    if (command instanceof Timeoutable) {
      var timeoutable = (Timeoutable) command;
      var timeout = Timeout.of(timeoutable.maxDuration());
      return Failsafe.with(timeout).get(exec -> next.invoke());
    } else {
      return next.invoke();
    }
  }
}
