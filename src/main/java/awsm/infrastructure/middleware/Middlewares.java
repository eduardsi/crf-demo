package awsm.infrastructure.middleware;

public interface Middlewares {

  <R, C extends Command<R>> R send(C command);

}
