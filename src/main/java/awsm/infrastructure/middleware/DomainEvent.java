package awsm.infrastructure.middleware;

import java.lang.reflect.Type;

public interface DomainEvent {

  default Type type() {
    return this.getClass();
  }

}
