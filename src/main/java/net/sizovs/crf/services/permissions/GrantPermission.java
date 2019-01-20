package net.sizovs.crf.services.permissions;

import lombok.Value;
import net.sizovs.crf.backbone.Command;

@Value
public class GrantPermission implements Command<Command.Void> {

    private final String memberId;
    private final String permissionId;

}
