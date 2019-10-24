package awsm.infrastructure.middleware;

public interface ScheduledCommand extends MiddlewareCommand<ReturnsNothing> {

  default void schedule() {
    SchedulerHolder.get().schedule(this);
  }

}
