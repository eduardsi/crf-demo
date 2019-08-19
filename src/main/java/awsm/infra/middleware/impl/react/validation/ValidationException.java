package awsm.infra.middleware.impl.react.validation;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

class ValidationException extends RuntimeException {

  private final List<String> violations;

  ValidationException(List<String> violations) {
    this.violations = violations;
  }

  @Override
  public String getMessage() {
    return String.join(", ", violations);
  }

  @ControllerAdvice
  static class SpringMvcHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<List<String>> handle(ValidationException it, WebRequest request) {
      return new ResponseEntity<>(it.violations, HttpStatus.BAD_REQUEST);
    }

  }

}