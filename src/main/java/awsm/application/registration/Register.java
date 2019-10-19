package awsm.application.registration;

import awsm.infrastructure.jackson.JacksonConstructor;
import awsm.infrastructure.middleware.Command;
import awsm.infrastructure.middleware.impl.resilience.RateLimit;
import awsm.infrastructure.modeling.DomainEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

public class Register implements Command<CharSequence> {

  public final String email;

  public final String firstName;

  public final String lastName;

  @JacksonConstructor
  public Register(String email, String firstName, String lastName) {
    this.email = email;
    this.firstName = firstName;
    this.lastName = lastName;
  }

  @RestController
  static class Http {
    @PostMapping("/customers")
    CharSequence post(@RequestBody Register command) {
      return command.execute();
    }
  }

  @Component
  static class Resilience implements RateLimit<Register> {

    private final int rateLimit;

    public Resilience(@Value("${registration.rateLimit}") int rateLimit) {
      this.rateLimit = rateLimit;
    }

    @Override
    public int rateLimit() {
      return rateLimit;
    }
  }

  public static class RegistrationCompleted implements DomainEvent {

    public final String fullName;
    public final String email;

    public RegistrationCompleted(String fullName, String email) {
      this.fullName = fullName;
      this.email = email;
    }

  }

}
