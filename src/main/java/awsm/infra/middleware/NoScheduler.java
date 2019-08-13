package awsm.infra.middleware;

class NoScheduler implements Scheduler {

  @Override
  public void schedule(Command command) {
    throw new NoSchedulerException(command.getClass().getSimpleName());
  }
}
