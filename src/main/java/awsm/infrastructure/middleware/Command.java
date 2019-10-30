package awsm.infrastructure.middleware;

import static java.lang.String.format;

public interface Command<R>  {

  default String id() {
    throw new UnsupportedOperationException(
        format("Command %s does not have an id", getClass().getSimpleName())
    );
  }

  default R execute() {
    return MiddlewaresHolder.get().send(this);
  }

  default void schedule() {
    SchedulerHolder.get().schedule(this);
  }


}
