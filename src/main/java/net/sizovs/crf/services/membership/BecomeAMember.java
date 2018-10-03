package net.sizovs.crf.services.membership;

import net.sizovs.crf.backbone.Command;

public class BecomeAMember implements Command<MemberId> {

    private final String email;

    public BecomeAMember(String email) {
        this.email = email;
    }

    public String email() {
        return email;
    }
}
