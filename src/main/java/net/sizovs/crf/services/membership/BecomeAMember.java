package net.sizovs.crf.services.membership;

import net.sizovs.crf.backbone.Command;

public class BecomeAMember implements Command<BecomeAMember.MemberId> {

    private final String email;

    public BecomeAMember(String email) {
        this.email = email;
    }

    public String email() {
        return email;
    }

    public static class MemberId implements Command.R {

        private final String id;

        public MemberId(String id) {
            this.id = id;
        }

        @Override
        public String toString() {
            return id;
        }
    }
}
