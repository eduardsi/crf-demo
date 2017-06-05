package net.sizovs.crf.services.permissions;

import net.sizovs.crf.backbone.Command;

public class CreatePermission implements Command<CreatePermission.PermissionId> {

    private final String name;

    public CreatePermission(String name) {
        this.name = name;
    }

    public String name() {
        return name;
    }

    public static class PermissionId implements Command.R {

        private final String id;

        public PermissionId(String id) {
            this.id = id;
        }

        @Override
        public String toString() {
            return id;
        }
    }

}
