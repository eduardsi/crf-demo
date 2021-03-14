package awsm.api;

import static awsm.infrastructure.security.Encryption.encrypt;

import awsm.domain.crm.Customer;
import awsm.domain.crm.CustomerRepository;
import awsm.domain.crm.UniqueEmail;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import javax.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Transactional
class RegistrationController {

  private final Validator validator;
  private final CustomerRepository repo;

  RegistrationController(Validator validator, CustomerRepository repo) {
    this.validator = validator;
    this.repo = repo;
  }

  @PostMapping("/registrations")
  public ResponseDto register(@RequestBody RequestDto request) {
    var violations = validator.validate(request);
    if (!violations.isEmpty()) {
      throw new ConstraintViolationException(violations);
    }

    var customer =
        new Customer(request.personalId, request.firstName, request.lastName, request.email);
    repo.save(customer);

    return new ResponseDto(encrypt(request.personalId));
  }

  @Data
  @Accessors(fluent = true)
  static class RequestDto {
    @NotEmpty public final String firstName;
    @NotEmpty public final String lastName;
    @NotEmpty public final String personalId;
    @NotEmpty @UniqueEmail public final String email;
  }

  @Data
  @Accessors(fluent = true)
  static class ResponseDto {
    public final String personalId;
  }
}
