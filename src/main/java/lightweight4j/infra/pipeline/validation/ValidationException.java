package lightweight4j.infra.pipeline.validation;

import an.awesome.pipelinr.Command;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.validation.ConstraintViolation;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

public class ValidationException extends RuntimeException {

    private final Set<ConstraintViolation<? extends Command>> violations;

    <C extends Command> ValidationException(Set<ConstraintViolation<C>> violations) {
        this.violations = new LinkedHashSet<>(violations.size());
        this.violations.addAll(violations);
    }

    @Override
    public String getMessage() {
        return violations
                .stream()
                .map(ViolatedProperty::new)
                .map(ViolatedProperty::toString)
                .collect(joining(", "));
    }

    private static class ViolatedProperty {

        private String property;
        private String message;

        ViolatedProperty(ConstraintViolation violation) {
            this.property = violation.getPropertyPath().toString();
            this.message = violation.getMessage();
        }

        @Override
        public String toString() {
            return String.format("%s %s", property, message);
        }
    }

    @ControllerAdvice
    static class SpringMvcHandler extends ResponseEntityExceptionHandler {

        @ExceptionHandler(ValidationException.class)
        public ResponseEntity<List<ViolatedProperty>> handle(ValidationException it, WebRequest request) {
            var violatedProperties = it.violations.stream().map(ViolatedProperty::new).collect(toList());
            return new ResponseEntity<>(violatedProperties, HttpStatus.BAD_REQUEST);
        }

    }

}