package lightweight4j.features.permissions.impl;

public class DuplicatePermissionCreationException extends RuntimeException {

    public DuplicatePermissionCreationException(String name) {
        super("Permission with a given name already exists (" + name + ")");
    }
}
