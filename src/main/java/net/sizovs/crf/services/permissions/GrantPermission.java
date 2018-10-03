package net.sizovs.crf.services.permissions;

import net.sizovs.crf.backbone.Command;
import net.sizovs.crf.services.membership.MemberId;

public class GrantPermission implements Command<Command.R.Void> {

    private final String memberId;
    private final String permissionId;

    public GrantPermission(MemberId memberId, PermissionId permissionId) {
        this.permissionId = permissionId.toString();
        this.memberId = memberId.toString();
    }

    public String memberId() {
        return memberId;
    }

    public String permissionId() {
        return permissionId;
    }
}
