package lightweight4j.app.permissions;

import lombok.Value;
import lightweight4j.lib.commands.Command;

@Value
public class GrantPermission implements Command<Command.Void> {

    private final String memberId;
    private final String permissionId;

}
