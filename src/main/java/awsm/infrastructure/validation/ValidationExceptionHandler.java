package awsm.infrastructure.validation;

import static java.util.stream.Collectors.toMap;

import java.util.Map;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Path;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
class ValidationExceptionHandler extends ResponseEntityExceptionHandler {

  @ExceptionHandler(ConstraintViolationException.class)
  protected ResponseEntity<Map<Path, String>> handle(
      ConstraintViolationException ex, WebRequest request) {
    var errors =
        ex.getConstraintViolations().stream()
            .collect(toMap(ConstraintViolation::getPropertyPath, ConstraintViolation::getMessage));
    return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
  }
}
