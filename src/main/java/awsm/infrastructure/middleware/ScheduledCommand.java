package awsm.infrastructure.middleware;

public interface ScheduledCommand extends MiddlewareCommand<ReturnsNothing> {

  default void later() {
    SchedulerHolder.get().schedule(this);
  }

}
