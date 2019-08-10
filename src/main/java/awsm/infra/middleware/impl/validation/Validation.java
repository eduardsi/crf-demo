package awsm.infra.middleware.impl.validation;

import static javax.validation.Validation.buildDefaultValidatorFactory;

import awsm.infra.middleware.Command;
import awsm.infra.middleware.Middleware;
import javax.validation.Validator;
import org.springframework.stereotype.Component;

@Component
public class Validation implements Middleware {

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