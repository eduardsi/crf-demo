package awsm.infrastructure.mvc;

import awsm.infrastructure.validation.Validator;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.List;

@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ValidationExceptionHandler extends ResponseEntityExceptionHandler {

  @ExceptionHandler(Validator.ValidationException.class)
  public ResponseEntity<List<String>> handle(Validator.ValidationException it, WebRequest request) {
    return new ResponseEntity<>(it.violations(), HttpStatus.BAD_REQUEST);
  }

}