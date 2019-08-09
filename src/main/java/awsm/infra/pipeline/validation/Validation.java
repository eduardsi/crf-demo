package awsm.infra.pipeline.validation;

import static javax.validation.Validation.buildDefaultValidatorFactory;

import an.awesome.pipelinr.Command;
import an.awesome.pipelinr.PipelineStep;
import javax.validation.Validator;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(10)
class Validation implements PipelineStep {

  private final Validator validator;

  public Validation() {
    this.validator = buildDefaultValidatorFactory().getValidator();
  }

  @Override
  public <R, C extends Command<R>> R invoke(C command, Next<R> next) {

    var violations = validator.validate(command);
    if (!violations.isEmpty()) {
      throw new ValidationException(violations);
    }

    return next.invoke();

  }
}