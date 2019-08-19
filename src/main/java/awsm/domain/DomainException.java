package awsm.domain;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

public class DomainException extends RuntimeException {

  public DomainException(String message) {
    super(message);
  }

  @ControllerAdvice
  static class SpringMvcHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(DomainException.class)
    public ResponseEntity<String> handle(DomainException it, WebRequest request) {
      return new ResponseEntity<>(it.getLocalizedMessage(), HttpStatus.BAD_REQUEST);
    }

  }


}
