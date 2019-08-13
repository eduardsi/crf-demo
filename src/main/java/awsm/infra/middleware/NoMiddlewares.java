package awsm.infra.middleware;

class NoMiddlewares implements Middlewares {

  @Override
  public <R, C extends Command<R>> R send(C command) {
    throw new NoMiddlewaresException(command.getClass().getSimpleName());
  }

}
