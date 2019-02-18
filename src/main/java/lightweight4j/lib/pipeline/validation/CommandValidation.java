package lightweight4j.lib.pipeline.validation;

import an.awesome.pipelinr.Command;
import an.awesome.pipelinr.PipelineStep;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.validation.Validation;

@Component
@Order(4)
class CommandValidation implements PipelineStep {

    @Override
    public <R, C extends Command<R>> R invoke(C command, Next<R> next) {
        var validator = Validation.buildDefaultValidatorFactory().getValidator();
        var violations = validator.validate(command);
        if (!violations.isEmpty()) {
            throw new CommandValidationException(violations);
        }

        return next.invoke();

    }
}
