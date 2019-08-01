package lightweight4j.features.registration.impl;

import an.awesome.pipelinr.Command;
import lightweight4j.features.registration.Registration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
class RegistrationHandler implements Command.Handler<Registration, Long> {

    private final Members members;

    private final EmailBlacklist blacklist;

    RegistrationHandler(Members members, EmailBlacklist blacklist) {
        this.members = members;
        this.blacklist = blacklist;
    }

    @Override
    public Long handle(Registration cmd) {
        var email = new Email(cmd.email);
        if (blacklist.contains(email)) {
            throw new EmailIsBlacklistedException(email);
        }

        var name = new Name(cmd.firstName, cmd.lastName);
        var member = new Member(name, email);
        members.save(member);
        return member.id();
    }

}
