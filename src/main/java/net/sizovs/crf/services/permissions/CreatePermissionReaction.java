package net.sizovs.crf.services.permissions;

import net.sizovs.crf.backbone.Reaction;
import org.springframework.stereotype.Component;

@Component
class CreatePermissionReaction implements Reaction<CreatePermission, CreatePermission.PermissionId> {

    private final Permissions permissions;

    private final Permission.NameUniqueness nameUniqueness;

    public CreatePermissionReaction(Permissions permissions, Permission.NameUniqueness nameUniqueness) {
        this.permissions = permissions;
        this.nameUniqueness = nameUniqueness;
    }

    @Override
    public CreatePermission.PermissionId react(CreatePermission $) {
        Permission.UniqueName name = new Permission.UniqueName($.name(), nameUniqueness);
        Permission permission = new Permission(name);
        permissions.save(permission);

        return new CreatePermission.PermissionId(permission.id());
    }

}
