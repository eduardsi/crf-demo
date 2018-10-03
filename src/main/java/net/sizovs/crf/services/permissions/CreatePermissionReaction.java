package net.sizovs.crf.services.permissions;

import net.sizovs.crf.backbone.Reaction;
import org.springframework.stereotype.Component;

@Component
class CreatePermissionReaction implements Reaction<CreatePermission, PermissionId> {

    private final Permissions permissions;

    private final Permission.NameUniqueness nameUniqueness;

    public CreatePermissionReaction(Permissions permissions, Permission.NameUniqueness nameUniqueness) {
        this.permissions = permissions;
        this.nameUniqueness = nameUniqueness;
    }

    @Override
    public PermissionId react(CreatePermission $) {
        var name = new Permission.UniqueName($.name(), nameUniqueness);
        var permission = new Permission(name);
        permissions.save(permission);

        return new PermissionId(permission.id());
    }

}
