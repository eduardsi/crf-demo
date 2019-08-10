package awsm.infra.middleware;

public interface Middlewares {

  <R, C extends Command<R>> R send(C command);

}
