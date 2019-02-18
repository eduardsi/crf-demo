package lightweight4j.features.permissions;

import an.awesome.pipelinr.Command;
import lightweight4j.features.permissions.impl.Permission;
import lightweight4j.features.permissions.impl.Permissions;
import org.springframework.stereotype.Component;

public class CreatePermission implements Command<String> {

    private final String name;

    public CreatePermission(String name) {
        this.name = name;
    }

    @Component
    static class Handler implements Command.Handler<CreatePermission, String> {

        private final Permissions permissions;

        private final Permission.NameUniqueness nameUniqueness;

        public Handler(Permissions permissions, Permission.NameUniqueness nameUniqueness) {
            this.permissions = permissions;
            this.nameUniqueness = nameUniqueness;
        }

        @Override
        public String handle(CreatePermission $) {
            var name = new Permission.UniqueName($.name, nameUniqueness);
            var permission = new Permission(name);
            permissions.save(permission);

            return permission.id();
        }

    }
}
