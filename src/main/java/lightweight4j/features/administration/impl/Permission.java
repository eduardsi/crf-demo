package lightweight4j.features.administration.impl;

import lightweight4j.lib.domain.DomainEntity;
import lightweight4j.lib.hibernate.HibernateConstructor;

import javax.persistence.Entity;

import static java.lang.String.format;
import static java.util.EnumSet.allOf;

@Entity
class Permission extends DomainEntity {

    public enum Operation {
        BACKOFFICE_ADMINISTRATION
    }

    private Operation operation;

    private Permission(Operation operation) {
        this.operation = operation;
    }

    @HibernateConstructor
    private Permission() {
    }

    public static Permission toDo(String operationName) {
        var availableOperations = allOf(Operation.class);
        var operation = availableOperations
                .stream()
                .filter(it -> it.name().equals(operationName))
                .findFirst()
                .orElseThrow(() ->
                        new IllegalArgumentException(format("%s is not in the list of available operations %s", operationName, availableOperations)));

        return new Permission(operation);
    }

    public Operation operation() {
        return operation;
    }
}
