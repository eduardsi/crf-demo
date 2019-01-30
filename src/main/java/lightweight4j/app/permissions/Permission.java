package lightweight4j.app.permissions;

import org.springframework.stereotype.Component;

import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.UUID;

@Entity
public class Permission {

    @Id
    private String id = UUID.randomUUID().toString();

    @Embedded
    private UniqueName name;

    public Permission(UniqueName name) {
        this.name = name;
    }

    private Permission() {
    }

    public UniqueName name() {
        return name;
    }

    public String id() {
        return id;
    }


    @Embeddable
    public static class UniqueName {

        private String string;

        public UniqueName(String string, NameUniqueness uniqueness) {
            if (!uniqueness.guaranteed(string)) {
                throw new DuplicatePermissionCreationAttempted(string);
            }
            this.string = string;
        }

        private UniqueName() {
        }

        @Override
        public String toString() {
            return string;
        }
    }

    @Component
    public static class NameUniqueness {

        private final Permissions permissions;

        public NameUniqueness(Permissions permissions) {
            this.permissions = permissions;
        }

        public boolean guaranteed(String name) {
            return permissions.countByNameStringIgnoreCase(name) == 0;
        }

    }

}
