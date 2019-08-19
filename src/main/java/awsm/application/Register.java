package awsm.application;

import static org.springframework.beans.factory.config.BeanDefinition.SCOPE_PROTOTYPE;

import awsm.domain.registration.Email;
import awsm.domain.registration.EmailBlacklist;
import awsm.domain.registration.EmailBlacklistedException;
import awsm.domain.registration.Member;
import awsm.domain.registration.Members;
import awsm.domain.registration.Name;
import awsm.infra.jackson.JacksonConstructor;
import awsm.infra.middleware.Command;
import awsm.infra.middleware.impl.react.Reaction;
import awsm.infra.middleware.impl.resilience.RateLimit;
import awsm.infra.middleware.impl.react.validation.Validator;
import org.hashids.Hashids;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

class Register implements Command<String> {

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
  static class HttpEntryPoint {

    @PostMapping("/members")
    String accept(@RequestBody Register command) {
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
  static class Re implements Reaction<Register, String> {

    private final Members members;

    private final EmailBlacklist blacklist;

    private final Hashids hashids;

    private final Email.Uniqueness uniqueness;

    Re(Members members, EmailBlacklist blacklist, Hashids hashids, Email.Uniqueness uniqueness) {
      this.members = members;
      this.blacklist = blacklist;
      this.hashids = hashids;
      this.uniqueness = uniqueness.memoized();
    }

    @Override
    public String react(Register cmd) {
      new Validator<Register>()
          .with(c -> c.firstName, v -> !v.isBlank(), "firstName is missing")
          .with(c -> c.lastName, v -> !v.isBlank(), "lastName is missing")
          .with(c -> c.email, v -> !v.isBlank(), "email is missing", nested ->
              nested
                  .with(c -> new Email(c.email), uniqueness::guaranteed, "email is taken")
          ).check(cmd);


      var email = new Email.Unique(cmd.email, uniqueness);
      throwIfBlacklisted(email);

      var name = new Name(cmd.firstName, cmd.lastName);
      var member = new Member(name, email);
      members.save(member);
      return hashids.encode(member.id());
    }

    private void throwIfBlacklisted(Email email) {
      if (blacklist.contains(email)) {
        throw new EmailBlacklistedException(email);
      }
    }

  }

}
