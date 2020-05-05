package awsm.infrastructure.middleware.resilience;

import an.awesome.pipelinr.Command;
import com.google.common.reflect.TypeToken;

public interface RateLimit<C extends Command> {

  int rateLimit();

  default boolean matches(C command) {
    TypeToken<C> commandTypeInAGeneric = new TypeToken<>(getClass()) {
    };

    return commandTypeInAGeneric.isSubtypeOf(command.getClass());
  }

}
