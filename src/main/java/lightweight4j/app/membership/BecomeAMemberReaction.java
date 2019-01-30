package lightweight4j.app.membership;

import lightweight4j.lib.commands.Reaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
class BecomeAMemberReaction implements Reaction<BecomeAMember, String> {

    private final Members members;
    private final EmailBlacklist blacklist;

    @Autowired
    public BecomeAMemberReaction(Members members, EmailBlacklist blacklist) {
        this.members = members;
        this.blacklist = blacklist;
    }

    @Override
    public String react(BecomeAMember $) {
        var member = new Member(new Email($.email(), blacklist));
        System.out.println(member);
        members.save(member);

        return member.id();
    }

}