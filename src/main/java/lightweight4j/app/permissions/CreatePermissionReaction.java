package lightweight4j.app.permissions;

import lightweight4j.lib.commands.Reaction;
import org.springframework.stereotype.Component;

@Component
class CreatePermissionReaction implements Reaction<CreatePermission, String> {

    private final Permissions permissions;

    private final Permission.NameUniqueness nameUniqueness;

    public CreatePermissionReaction(Permissions permissions, Permission.NameUniqueness nameUniqueness) {
        this.permissions = permissions;
        this.nameUniqueness = nameUniqueness;
    }

    @Override
    public String react(CreatePermission $) {
        var name = new Permission.UniqueName($.name(), nameUniqueness);
        var permission = new Permission(name);
        permissions.save(permission);

        return permission.id();
    }

}
