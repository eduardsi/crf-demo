package awsm.infra.middleware.impl.react;

import awsm.infra.middleware.Command;
import com.google.common.reflect.TypeToken;

public interface Reaction<C extends Command<R>, R> {

  R react(C command);

  default boolean matches(C command) {
    TypeToken<C> commandTypeInAGeneric = new TypeToken<>(getClass()) {

    };
    return commandTypeInAGeneric.isSubtypeOf(command.getClass());
  }

}
