package awsm.infrastructure.middleware;

public interface MiddlewareCommand<R> extends Command<R> {

  @Override
  default R now() {
    return MiddlewaresHolder.get().send(this);
  }

}
