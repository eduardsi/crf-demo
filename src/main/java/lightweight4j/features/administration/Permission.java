package lightweight4j.features.administration;

import lightweight4j.lib.hibernate.HibernateConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.UUID;

import static java.util.EnumSet.allOf;

@Entity
class Permission {

    public enum Operation {
        BACKOFFICE_ADMINISTRATION
    }

    @Id
    private String id = UUID.randomUUID().toString();

    private Operation operation;

    private Permission(Operation operation) {
        this.operation = operation;
    }

    @HibernateConstructor
    private Permission() {
    }

    public String id() {
        return id;
    }


    public static Permission toDo(String operationName) {
        var availableOps = allOf(Operation.class);
        var operation = availableOps
                .stream()
                .filter(it -> it.name().equals(operationName))
                .findFirst()
                .orElseThrow(() ->
                        new IllegalArgumentException("No such operation " + operationName + ". Available operations: " + availableOps));

        return new Permission(operation);
    }

    public Operation operation() {
        return operation;
    }
}
