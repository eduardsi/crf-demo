package lightweight4j.infra.pipeline.validation;

import an.awesome.pipelinr.Command;
import an.awesome.pipelinr.PipelineStep;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.validation.Validator;

import static javax.validation.Validation.buildDefaultValidatorFactory;

@Component
@Order(2)
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