package awsm.infra.middleware;

public interface Command<R>  {

  default R execute() {
    return MiddlewaresHolder.get().send(this);
  }

}
