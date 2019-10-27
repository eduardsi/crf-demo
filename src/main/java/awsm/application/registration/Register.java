package awsm.application.registration;

import awsm.infrastructure.middleware.Command;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

public class Register implements Command<Register.Response> {

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
    Response post(@RequestBody Register register) {
      return register.execute();
    }
  }

  public static class Response {

    final String customerHashId;

    public Response(String customerHashId) {
      this.customerHashId = customerHashId;
    }
  }

}
