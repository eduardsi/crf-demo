package net.sizovs.crf.services.permissions;

import net.sizovs.crf.backbone.Command;

public class CreatePermission implements Command<PermissionId> {

    private final String name;

    public CreatePermission(String name) {
        this.name = name;
    }

    public String name() {
        return name;
    }



}
