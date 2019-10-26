package awsm.application.registration.domain;

import static awsm.infrastructure.memoization.Memoizers.memoized;
import static org.springframework.beans.factory.config.BeanDefinition.SCOPE_PROTOTYPE;

import awsm.application.registration.Register;
import awsm.infrastructure.hashing.HashId;
import awsm.infrastructure.middleware.impl.execution.Executor;
import awsm.infrastructure.validation.Validator;
import awsm.infrastructure.middleware.impl.resilience.RateLimit;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(SCOPE_PROTOTYPE)
class RegisterExecutor implements Executor<Register, CharSequence> {

  private final Customer.Repository repository;

  private final EmailBlacklist blacklist;

  private final EmailUniqueness uniqueness;

  RegisterExecutor(Customer.Repository repository, EmailBlacklist blacklist, EmailUniqueness uniqueness) {
    this.repository = repository;
    this.uniqueness = memoized(uniqueness::guaranteed)::apply;
    this.blacklist = memoized(blacklist::allows)::apply;
  }

  @Override
  public CharSequence execute(Register cmd) {

    var name = new FullName(cmd.firstName, cmd.lastName);
    var email = new Email(cmd.email, uniqueness, blacklist);

    var customer = new Customer(name, email);
    customer.register(repository);

    var welcome = new Welcome(customer.id());
    welcome.schedule();

    return new HashId(customer.id());
  }

  @Override
  public void validate(Register cmd) {
    new Validator<Register>()
        .with(() -> cmd.firstName, v -> !v.isBlank(), "firstName is missing")
        .with(() -> cmd.lastName, v -> !v.isBlank(), "lastName is missing")
        .with(() -> cmd.email, v -> !v.isBlank(), "email is missing", nested ->
            nested
                .with(() -> cmd.email, uniqueness::guaranteed, "email is taken")
                .with(() -> cmd.email, blacklist::allows,      "email %s is blacklisted")
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
