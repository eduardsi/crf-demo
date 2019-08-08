package awsm.domain.administration;

import static java.lang.String.format;
import static java.util.EnumSet.allOf;

import awsm.infra.hibernate.HibernateConstructor;
import awsm.infra.hibernate.HibernateEntity;
import javax.persistence.Entity;

@Entity
public class Permission extends HibernateEntity {

  enum Operation {
    BACKOFFICE_ADMINISTRATION
  }

  private Operation operation;

  private Permission(Operation operation) {
    this.operation = operation;
  }

  @HibernateConstructor
  private Permission() {
  }

  public static Permission toDo(String opName) {
    var availableOps = allOf(Operation.class);
    var operation = availableOps
            .stream()
            .filter(it -> it.name().equals(opName))
            .findFirst()
            .orElseThrow(() ->
                    new IllegalArgumentException(format("%s is not in the list of available operations %s",
                            opName, availableOps)));

    return new Permission(operation);
  }

  Operation operation() {
    return operation;
  }
}
