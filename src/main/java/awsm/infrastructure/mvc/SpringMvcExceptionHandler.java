package awsm.infrastructure.mvc;

import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import static java.util.Optional.ofNullable;
import static org.springframework.core.annotation.AnnotationUtils.getAnnotation;

@ControllerAdvice
@Order
class SpringMvcExceptionHandler extends ResponseEntityExceptionHandler {

  @ExceptionHandler(Exception.class)
  public ResponseEntity<String> handle(Exception it) {
    var responseStatus = getAnnotation(it.getClass(), ResponseStatus.class);
    var status = ofNullable(responseStatus).map(ResponseStatus::code).orElse(HttpStatus.BAD_REQUEST);
    return new ResponseEntity<>(it.getLocalizedMessage(), status);
  }

}
