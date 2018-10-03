package net.sizovs.crf.services.permissions;

import net.sizovs.crf.backbone.Command;

public class PermissionId implements Command.R {

    private final String id;

    public PermissionId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return id;
    }
}