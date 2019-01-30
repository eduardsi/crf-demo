package lightweight4j.app.permissions;

public class DuplicatePermissionCreationAttempted extends RuntimeException {

    public DuplicatePermissionCreationAttempted(String name) {
        super("Permission with a given name already exists (" + name + ")");
    }
}
