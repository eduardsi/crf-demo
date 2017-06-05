package net.sizovs.crf.services.permissions;

import net.sizovs.crf.backbone.Command;

public class GrantPermission implements Command<Command.R.Void> {

    private final String memberId;
    private final String permissionId;

    public GrantPermission(String memberId, String permissionId) {
        this.permissionId = permissionId;
        this.memberId = memberId;
    }

    public String memberId() {
        return memberId;
    }

    public String permissionId() {
        return permissionId;
    }
}
