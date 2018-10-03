package net.sizovs.crf.services.membership;

import net.sizovs.crf.backbone.Reaction;
import org.springframework.stereotype.Component;

@Component
class BecomeAMemberReaction implements Reaction<BecomeAMember, MemberId> {

    private final Members members;

    public BecomeAMemberReaction(Members members) {
        this.members = members;
    }

    @Override
    public MemberId react(BecomeAMember $) {

        var member = new Member($.email());
        members.save(member);

        return new MemberId(member.id());
    }
}
