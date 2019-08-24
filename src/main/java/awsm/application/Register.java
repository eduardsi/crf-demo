package awsm.application;

import static awsm.infra.memoization.Memoizers.memoizer;
import static org.springframework.beans.factory.config.BeanDefinition.SCOPE_PROTOTYPE;

import awsm.domain.administration.Administrator;
import awsm.domain.administration.Administrators;
import awsm.domain.registration.Email;
import awsm.domain.registration.Email.NotBlacklisted;
import awsm.domain.registration.Email.Unique;
import awsm.domain.registration.Member;
import awsm.domain.registration.Members;
import awsm.domain.registration.Name;
import awsm.infra.hashing.HashId;
import awsm.infra.jackson.JacksonConstructor;
import awsm.infra.middleware.Command;
import awsm.infra.middleware.impl.react.Reaction;
import awsm.infra.middleware.impl.react.validation.Validator;
import awsm.infra.middleware.impl.resilience.RateLimit;
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
    @PostMapping("/members")
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

    private final Members members;

    private final Administrators administrators;

    private final Email.Blacklist blacklist;

    private final Email.Uniqueness uniqueness;

    Re(Members members, Administrators administrators, Email.Blacklist blacklist, Email.Uniqueness uniqueness) {
      this.members = members;
      this.administrators = administrators;
      this.uniqueness = memoizer(uniqueness::guaranteed)::memoized;
      this.blacklist = memoizer(blacklist::allows)::memoized;
    }

    @Override
    public CharSequence react(Register cmd) {

      var email = memoizer(() -> new Email(cmd.email));

      new Validator<Register>()
          .with(() -> cmd.firstName, v -> !v.isBlank(), "firstName is missing")
          .with(() -> cmd.lastName, v -> !v.isBlank(), "lastName is missing")
          .with(() -> cmd.email, v -> !v.isBlank(), "email is missing", nested ->
              nested
                  .with(email::memoized, uniqueness::guaranteed, "email is taken")
                  .with(email::memoized, blacklist::allows,      "email %s is blacklisted")
          ).check(cmd);

      var name = new Name(cmd.firstName, cmd.lastName);
      var registrationEmail =
          new NotBlacklisted(blacklist,
              new Unique(uniqueness,
                  email.memoized()));

      var member = new Member(name, registrationEmail);
      members.save(member);

      var admin = new Administrator(member.id());
      administrators.save(admin);

      var welcome = new Welcome(member.id());
      welcome.schedule();

      return new HashId(member.id());
    }

  }

}
