package awsm.infra.middleware;

import static java.lang.String.format;

class NoSchedulerException extends RuntimeException {

  NoSchedulerException(String cmd) {
    super(format(
        "%s has not been scheduled, because no scheduler is set. For production usage, set global %s.",
        cmd, Scheduler.class.getSimpleName()));
  }
}
