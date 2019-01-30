package lightweight4j.app.permissions;

import lombok.Value;
import lightweight4j.lib.commands.Command;

@Value
public class CreatePermission implements Command<String> {

    private final String name;

}
