package awsm.application.customer;

import static awsm.infra.memoization.Memoizers.memoized;
import static awsm.infra.middleware.ReturnsNothing.NOTHING;
import static org.springframework.beans.factory.config.BeanDefinition.SCOPE_PROTOTYPE;

import awsm.domain.administration.Administrator;
import awsm.domain.administration.Administrators;
import awsm.domain.registration.Email;
import awsm.domain.registration.Customer;
import awsm.domain.registration.Customers;
import awsm.domain.registration.FullName;
import awsm.infra.hashing.HashId;
import awsm.infra.jackson.JacksonConstructor;
import awsm.infra.middleware.Command;
import awsm.infra.middleware.ReturnsNothing;
import awsm.infra.middleware.impl.react.Reaction;
import awsm.infra.middleware.impl.react.validation.Validator;
import awsm.infra.middleware.impl.resilience.RateLimit;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

class Register implements Command<CharSequence> {

  private final String email;

  private final String firstName;

  private final String lastName;

  @JacksonConstructor
  public Register(String email, String firstName, String lastName) {
    this.email = email;
    this.firstName = firstName;
    this.lastName = lastName;
  }

  @RestController
  static class Http {
    @PostMapping("/customers")
    CharSequence accept(@RequestBody Register command) {
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

  @Component
  @Scope(SCOPE_PROTOTYPE)
  static class Re implements Reaction<Register, CharSequence> {

    private final Customers customers;

    private final Administrators administrators;

    private final Email.Blacklist blacklist;

    private final Email.Uniqueness uniqueness;

    Re(Customers customers, Administrators administrators, Email.Blacklist blacklist, Email.Uniqueness uniqueness) {
      this.customers = customers;
      this.administrators = administrators;
      this.uniqueness = memoized(uniqueness::guaranteed)::apply;
      this.blacklist = memoized(blacklist::allows)::apply;
    }

    @Override
    public CharSequence react(Register cmd) {

      new Validator<Register>()
          .with(() -> cmd.firstName, v -> !v.isBlank(), "firstName is missing")
          .with(() -> cmd.lastName, v -> !v.isBlank(), "lastName is missing")
          .with(() -> cmd.email, v -> !v.isBlank(), "email is missing", nested ->
              nested
                  .with(() -> cmd.email, uniqueness::guaranteed, "email is taken")
                  .with(() -> cmd.email, blacklist::allows,      "email %s is blacklisted")
          ).check(cmd);

      var name = new FullName(cmd.firstName, cmd.lastName);
      var email = new Email(cmd.email, uniqueness, blacklist);

      var customer = new Customer(name, email);
      customers.add(customer);

      var admin = new Administrator(customer.id());
      administrators.add(admin);

      var welcome = new Welcome(customer.id());
      welcome.schedule();

      return new HashId(customer.id());
    }

  }

  static class Welcome implements Command<ReturnsNothing> {

    private final long customerId;

    Welcome(@JsonProperty("customerId") long customerId) {
      this.customerId = customerId;
    }

    @Component
    static class Re implements Reaction<Welcome, ReturnsNothing> {

      private final Customers customers;

      private Re(Customers customers) {
        this.customers = customers;
      }

      @Override
      public ReturnsNothing react(Welcome cmd) {
        var customer = customers.singleById(cmd.customerId).orElseThrow();
        System.out.printf("Sending email to %s: Welcome to the Matrix, %s", customer.email(), customer.name());
        return NOTHING;
      }
    }
  }
}
