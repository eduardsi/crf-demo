package net.sizovs.crf.services.membership;
import net.sizovs.crf.backbone.Command;

public class MemberId implements Command.R {

    private final String id;

    public MemberId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return id;
    }
}