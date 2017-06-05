package net.sizovs.crf.services.permissions;

public class PermissionAlreadyExists extends RuntimeException {

    public PermissionAlreadyExists(String name) {
        super("Permission with a given name already exists (" + name + ")");
    }
}
