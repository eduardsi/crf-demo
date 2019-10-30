package awsm.crm;

import static awsm.crm.Register.WelcomeNewCustomer.ID;
import static awsm.infrastructure.memoization.Memoizers.memoized;
import static org.springframework.beans.factory.config.BeanDefinition.SCOPE_PROTOTYPE;

import an.awesome.pipelinr.Command;
import an.awesome.pipelinr.Pipeline;
import an.awesome.pipelinr.Voidy;
import awsm.infrastructure.middleware.MoreWork;
import awsm.infrastructure.middleware.resilience.RateLimit;
import awsm.infrastructure.middleware.scheduler.ScheduledCommandId;
import awsm.infrastructure.middleware.validation.Validator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

public class Register implements Command<Register.R> {

  private final String email;

  private final String firstName;

  private final String lastName;

  public Register(String email, String firstName, String lastName) {
    this.email = email;
    this.firstName = firstName;
    this.lastName = lastName;
  }

  @RestController
  static class OverHttp {

    private final Pipeline pipeline;

    OverHttp(Pipeline pipeline) {
      this.pipeline = pipeline;
    }

    @PostMapping("/customers")
    R post(@RequestBody Register register) {
      return register.execute(pipeline);
    }
  }

  static class R {

    final String customerHashId;

    R(String customerHashId) {
      this.customerHashId = customerHashId;
    }
    
  }

  @Component
  @Scope(SCOPE_PROTOTYPE)
  static class H implements Handler<Register, R> {

    private final Customer.Repository repository;

    private final EmailBlacklist blacklist;

    private final EmailUniqueness uniqueness;

    private final MoreWork moreWork;

    H(Customer.Repository repository, EmailBlacklist blacklist, EmailUniqueness uniqueness, MoreWork moreWork) {
      this.repository = repository;
      this.uniqueness = memoized(uniqueness::guaranteed)::apply;
      this.blacklist = memoized(blacklist::allows)::apply;
      this.moreWork = moreWork;
    }


    @Override
    public R handle(Register cmd) {
      validate(cmd);

      var name = new FullName(cmd.firstName, cmd.lastName);
      var email = new RegistrationEmail(cmd.email, uniqueness, blacklist);

      var customer = new Customer(name, email);
      customer.register(repository);

      var customerId = customer.id();

      var welcome = new WelcomeNewCustomer(customerId.asLong());
      var registrationCompleted = new RegistrationCompleted(customer.name() + "", customer.email() + "");

      moreWork.outbox(welcome);
      moreWork.notify(registrationCompleted);

      return new R(customerId.hashIdString());

    }

    private void validate(Register cmd) {
      var email = memoized(() -> new Email(cmd.email));
      new Validator<Register>()
          .with(() -> cmd.firstName, v -> !v.isBlank(), "firstName is missing")
          .with(() -> cmd.lastName, v -> !v.isBlank(), "lastName is missing")
          .with(() -> cmd.email, v -> !v.isBlank(), "email is missing", nested ->
              nested
                  .with(email, uniqueness::guaranteed, "email is taken")
                  .with(email, blacklist::allows,      "email %s is blacklisted")
          ).check(cmd);
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

  }

  @ScheduledCommandId(ID)
  static class WelcomeNewCustomer implements Command<Voidy> {

    static final String ID = "Welcome";

    private final long customerId;

    WelcomeNewCustomer(long customerId) {
      this.customerId = customerId;
    }

    @Component
    static class H implements Command.Handler<WelcomeNewCustomer, Voidy> {

      private final Customer.Repository customers;

      private H(Customer.Repository customers) {
        this.customers = customers;
      }

      @Override
      public Voidy handle(WelcomeNewCustomer cmd) {
        var customerId = new CustomerId(cmd.customerId);
        var customer = customers.singleBy(customerId);
        System.out.printf("Sending email to %s: Welcome to the Matrix, %s", customer.email(), customer.name());
        return new Voidy();
      }
    }
  }
}
