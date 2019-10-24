package awsm.infrastructure.middleware;

public interface Command<R>  {

  R execute();

}
