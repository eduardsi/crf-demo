package awsm.infrastructure.middleware;

public interface Command<R>  {

  default R execute() {
    return MiddlewaresHolder.get().send(this);
  }

  default void schedule() {
    SchedulerHolder.get().schedule(this);
  }

}
