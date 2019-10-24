package awsm.application.registration.impl;

import static awsm.infrastructure.memoization.Memoizers.memoized;
import static awsm.infrastructure.middleware.ReturnsNothing.NOTHING;
import static org.springframework.beans.factory.config.BeanDefinition.SCOPE_PROTOTYPE;

import awsm.application.registration.Register;
import awsm.infrastructure.hashing.HashId;
import awsm.infrastructure.middleware.ReturnsNothing;
import awsm.infrastructure.middleware.ScheduledCommand;
import awsm.infrastructure.middleware.impl.react.Reaction;
import awsm.infrastructure.middleware.impl.react.validation.Validator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(SCOPE_PROTOTYPE)
class RegisterReaction implements Reaction<Register, CharSequence> {

  private final Customers customers;

  private final Email.Blacklist blacklist;

  private final Email.Uniqueness uniqueness;

  RegisterReaction(Customers customers, Email.Blacklist blacklist, Email.Uniqueness uniqueness) {
    this.customers = customers;
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
    var customerId = customers.add(customer);

    var welcome = new Welcome(customerId);
    welcome.schedule();

    return new HashId(customerId);
  }

  static class Welcome implements ScheduledCommand {

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
        var customer = customers.singleById(cmd.customerId);
        System.out.printf("Sending email to %s: Welcome to the Matrix, %s", customer.email(), customer.name());
        return NOTHING;
      }
    }
  }
}
