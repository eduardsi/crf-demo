package awsm.infra.middleware.impl.react;

import awsm.infra.middleware.Command;
import com.google.common.reflect.TypeToken;

public interface Reaction<C extends Command<R>, R> {

  R react(C cmd);

  default boolean matches(C cmd) {
    TypeToken<C> commandTypeInAGeneric = new TypeToken<>(getClass()) {

    };
    return commandTypeInAGeneric.isSubtypeOf(cmd.getClass());
  }

}
