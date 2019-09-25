package awsm.infra.middleware.impl.resilience;

import awsm.infra.middleware.Command;
import com.google.common.reflect.TypeToken;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public interface RateLimit<C extends Command> {

  int rateLimit();

  default Optional<TimeUnit> timeUnit() {
    return Optional.empty();
  }

  default boolean matches(C command) {
    TypeToken<C> commandTypeInAGeneric = new TypeToken<>(getClass()) {
    };

    return commandTypeInAGeneric.isSubtypeOf(command.getClass());
  }

}
