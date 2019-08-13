package awsm.application;

import awsm.domain.registration.Email;
import awsm.domain.registration.EmailBlacklist;
import awsm.domain.registration.EmailBlacklistedException;
import awsm.domain.registration.Member;
import awsm.domain.registration.Members;
import awsm.domain.registration.Name;
import awsm.infra.middleware.Command;
import awsm.infra.middleware.impl.react.Reaction;
import awsm.infra.middleware.impl.resilience.RateLimit;
import javax.validation.constraints.NotEmpty;
import org.hashids.Hashids;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

class Register implements Command<String> {

  @NotEmpty
  private final String email;

  @NotEmpty
  private final String firstName;

  @NotEmpty
  private final String lastName;

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
  static class Re implements Reaction<Register, String> {

    private final Members members;

    private final EmailBlacklist blacklist;

    private final Hashids hashids;

    Re(Members members, EmailBlacklist blacklist, Hashids hashids) {
      this.members = members;
      this.blacklist = blacklist;
      this.hashids = hashids;
    }

    @Override
    public String react(Register cmd) {
      var email = new Email(cmd.email);
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
