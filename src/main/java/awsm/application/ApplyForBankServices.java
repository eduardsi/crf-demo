package awsm.application;

import an.awesome.pipelinr.Command;
import an.awesome.pipelinr.Pipeline;
import an.awesome.pipelinr.Voidy;
import awsm.domain.banking.customer.Email;
import awsm.domain.banking.customer.Customer;
import awsm.domain.banking.customer.Name;
import awsm.domain.banking.customer.blacklist.Blacklist;
import awsm.infrastructure.middleware.resilience.RateLimit;
import awsm.infrastructure.middleware.validation.Validator;
import awsm.infrastructure.time.TimeMachine;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

import static awsm.infrastructure.memoization.Memoizers.memoized;
import static java.time.Period.between;
import static org.springframework.beans.factory.config.BeanDefinition.SCOPE_PROTOTYPE;

public class ApplyForBankServices implements Command<Voidy> {

  private final String email;

  private final String firstName;

  private final String lastName;

  private final String countryOfResidence;

  private final String dateOfBirth;

  public ApplyForBankServices(String email, String firstName, String lastName, String countryOfResidence, String dateOfBirth) {
    this.email = email;
    this.firstName = firstName;
    this.lastName = lastName;
    this.countryOfResidence = countryOfResidence;
    this.dateOfBirth = dateOfBirth;
  }

  @RestController
  static class WebApi {
    private final Pipeline pipeline;

    WebApi(Pipeline pipeline) {
      this.pipeline = pipeline;
    }

    @PostMapping("/applications")
    Voidy post(@RequestBody ApplyForBankServices cmd) {
      return cmd.execute(pipeline);
    }
  }

  @Component
  static class Resilience implements RateLimit<ApplyForBankServices> {
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
  static class Handler implements Command.Handler<ApplyForBankServices, Voidy> {

    private final Blacklist blacklist;
    private final Email.Uniqueness uniqueness;
    private final Customer.Repository customerRepo;

    Handler(Blacklist blacklist, Email.Uniqueness uniqueness, Customer.Repository customerRepo) {
      this.customerRepo = customerRepo;
      this.uniqueness = memoized(uniqueness::guaranteed)::apply;
      this.blacklist = memoized(blacklist::permits)::apply;
    }

    @Override
    public Voidy handle(ApplyForBankServices cmd) {
      validate(cmd);

      // todo: make sure <= 1 pending applications

      var customer = newCustomer(cmd);
      customer.saveNew(customerRepo);

      return new Voidy();
    }

    private void validate(ApplyForBankServices cmd) {
      var email = memoized(() -> new Email(cmd.email));
      new Validator<ApplyForBankServices>()
              .with(() -> cmd.firstName, this::isNotBlank, "firstName is missing")
              .with(() -> cmd.lastName, this::isNotBlank, "lastName is missing")
              .with(() -> cmd.email, this::isNotBlank, "email is missing", nested ->
                      nested
                              .with(email, uniqueness::guaranteed, "email is taken")
                              .with(email, blacklist::permits, "email %s is blacklisted")
              )
              .with(() -> cmd.countryOfResidence, this::isCountryOk, "countryOfResidence is missing")
              .with(() -> cmd.dateOfBirth, this::isDateOk, "invalid date", nested ->
                      nested
                              .with(() -> cmd.dateOfBirth, this::isAdult, "you need to be at least 18")
              )
              .check(cmd);
    }

    private boolean isCountryOk(String country) {
      return country.length() == 2;
    }

    private boolean isNotBlank(String string) {
      return !string.isBlank();
    }

    private boolean isDateOk(String date) {
      var datePattern = "^\\d{4}-\\d{2}-\\d{2}$";
      return date.matches(datePattern);
    }

    private boolean isAdult(String dateOfBirth) {
      var today = TimeMachine.today();
      var birthday = LocalDate.parse(dateOfBirth);
      return between(birthday, today).getYears() >= 18;
    }

    private Customer newCustomer(ApplyForBankServices cmd) {
      var name = new Name(cmd.firstName, cmd.lastName);
      var email = new Email(cmd.email);
      var customer = new Customer(name, email, LocalDate.parse(cmd.dateOfBirth), cmd.countryOfResidence);
      return customer;
    }


  }
}
