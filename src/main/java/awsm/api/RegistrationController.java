package awsm.api;

import static awsm.infrastructure.security.Encryption.encrypt;
import static org.springframework.beans.factory.config.BeanDefinition.SCOPE_PROTOTYPE;

import awsm.domain.crm.Customer;
import awsm.domain.crm.CustomerRepository;
import awsm.domain.crm.UniqueEmail;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.context.annotation.Scope;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Scope(SCOPE_PROTOTYPE)
public class RegistrationController {

  private final CustomerRepository repo;

  RegistrationController(CustomerRepository repo) {
    this.repo = repo;
  }

  @PostMapping("/registrations")
  ResponseDto register(@Valid @RequestBody RequestDto request) {
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
