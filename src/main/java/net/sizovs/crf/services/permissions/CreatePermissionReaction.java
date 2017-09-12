package net.sizovs.crf.services.permissions;

import net.sizovs.crf.backbone.Reaction;
import org.springframework.stereotype.Component;

@Component
class CreatePermissionReaction implements Reaction<CreatePermission, CreatePermission.PermissionId> {

    private final Permissions permissions;

    public CreatePermissionReaction(Permissions permissions) {
        this.permissions = permissions;
    }

    @Override
    public CreatePermission.PermissionId react(CreatePermission $) {

        throwIfPermissionWithTheSameNameExists($);

        Permission permission = new Permission($.name());
        permissions.save(permission);

        return new CreatePermission.PermissionId(permission.id());
    }

    private void throwIfPermissionWithTheSameNameExists(CreatePermission $) {
        long permission = permissions.countByNameIgnoreCase($.name());
        if (permission > 0) {
            throw new DuplicatePermissionCreationAttempted($.name());
        }
    }
}
