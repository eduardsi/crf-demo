package awsm.infrastructure.middleware;

import com.google.common.reflect.TypeToken;

public interface SideEffect<T extends DomainEvent> {

  void invoke(T event);

  default TypeToken<T> eventType() {
    return new TypeToken<>(getClass()) {
    };
  }
}
