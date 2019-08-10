package awsm.infra.middleware.impl.react;

import awsm.infra.middleware.Command;
import awsm.infra.middleware.impl.resilience.Resilience;
import com.google.common.reflect.TypeToken;

public interface Reaction<C extends Command<R>, R> {

  R react(C command);

  default Resilience<R> resilience() {
    return new Resilience<>();
  }

  default boolean matches(C command) {
    TypeToken<C> commandTypeInAGeneric = new TypeToken<>(getClass()) {

    };
    return commandTypeInAGeneric.isSubtypeOf(command.getClass());
  }

}
