package awsm.infrastructure.middleware;

public interface MiddlewareCommand<R> extends Command<R> {

  default R execute() {
    return MiddlewaresHolder.get().send(this);
  }

}
