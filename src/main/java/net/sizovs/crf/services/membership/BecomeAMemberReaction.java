package net.sizovs.crf.services.membership;

import net.sizovs.crf.backbone.Reaction;
import org.springframework.stereotype.Component;

@Component
class BecomeAMemberReaction implements Reaction<BecomeAMember, String> {

    private final Members members;
    private final EmailBlacklist blacklist;

    public BecomeAMemberReaction(Members members, EmailBlacklist blacklist) {
        this.members = members;
        this.blacklist = blacklist;
    }

    @Override
    public String react(BecomeAMember $) {
        var member = new Member(new Email($.email(), blacklist));
        members.save(member);

        return member.id();
    }
}
