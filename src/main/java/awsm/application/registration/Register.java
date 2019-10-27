package awsm.application.registration;

import awsm.application.registration.domain.CustomerId;
import awsm.infrastructure.hashing.Hash;
import awsm.infrastructure.middleware.Command;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

public class Register implements Command<Hash<CustomerId>> {

  public final String email;

  public final String firstName;

  public final String lastName;

  public Register(String email, String firstName, String lastName) {
    this.email = email;
    this.firstName = firstName;
    this.lastName = lastName;
  }

  @RestController
  static class OverHttp {
    @PostMapping("/customers")
    Hash<CustomerId> post(@RequestBody Register register) {
      return register.execute();
    }
  }

}
