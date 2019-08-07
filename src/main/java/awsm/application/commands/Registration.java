package awsm.application.commands;

import awsm.domain.registration.Email;
import awsm.domain.registration.EmailBlacklist;
import awsm.domain.registration.EmailBlacklisted;
import awsm.domain.registration.Member;
import awsm.domain.registration.Members;
import awsm.domain.registration.Name;
import awsm.infra.pipeline.ExecutableCommand;
import awsm.infra.pipeline.tx.Tx;
import java.util.Collection;
import javax.validation.constraints.NotEmpty;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

public class Registration extends ExecutableCommand<Long> {

  @NotEmpty
  private final String email;

  @NotEmpty
  private final String firstName;

  @NotEmpty
  private final String lastName;

  public Registration(String email, String firstName, String lastName) {
    this.email = email;
    this.firstName = firstName;
    this.lastName = lastName;
  }

  @Component
  @RestController
  static class HttpEndpoint {

    @PostMapping("/members")
    Long accept(@RequestBody Registration command) {
      return new Tx<>(command).execute();
    }

  }

  @Component
  static class RegistrationHandler implements Handler<Registration, Long> {

    private final Members members;

    private final EmailBlacklist blacklist;

    private final Collection<Transaction> txHooks;

    RegistrationHandler(Members members, EmailBlacklist blacklist, Collection<Transaction> txHooks) {
      this.members = members;
      this.blacklist = blacklist;
      this.txHooks = txHooks;
    }

    @Override
    public Long handle(Registration cmd) {
      var email = new Email(cmd.email);
      throwIfBlacklisted(email);

      var name = new Name(cmd.firstName, cmd.lastName);
      var member = new Member(name, email);
      members.save(member);

      // hmm...
      txHooks.forEach(hook -> hook.joinTransaction(member.id()));
      return member.id();
    }

    private void throwIfBlacklisted(Email email) {
      if (blacklist.contains(email)) {
        throw new EmailBlacklisted(email);
      }
    }

  }

  public interface Transaction {

    void joinTransaction(Long memberId);

  }
}
