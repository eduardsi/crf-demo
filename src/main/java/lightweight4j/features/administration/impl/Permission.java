package lightweight4j.features.administration.impl;

import lightweight4j.lib.hibernate.HibernateEntity;

import javax.persistence.Entity;

import static java.lang.String.format;
import static java.util.EnumSet.allOf;

@Entity
class Permission extends HibernateEntity {

    enum Operation {
        BACKOFFICE_ADMINISTRATION
    }

    private Operation operation;

    private Permission(Operation operation) {
        this.operation = operation;
    }

    private Permission() {
    }

    static Permission toDo(String operationName) {
        var availableOperations = allOf(Operation.class);
        var operation = availableOperations
                .stream()
                .filter(it -> it.name().equals(operationName))
                .findFirst()
                .orElseThrow(() ->
                        new IllegalArgumentException(format("%s is not in the list of available operations %s", operationName, availableOperations)));

        return new Permission(operation);
    }

    Operation operation() {
        return operation;
    }
}
