package awsm.infrastructure.middleware.impl.execution;

import awsm.infrastructure.middleware.Command;
import com.google.common.reflect.TypeToken;

public interface Executor<C extends Command<R>, R> {

  R execute(C cmd);

  default boolean matches(C cmd) {
    TypeToken<C> commandTypeInAGeneric = new TypeToken<>(getClass()) {
    };
    return commandTypeInAGeneric.isSubtypeOf(cmd.getClass());
  }

}
