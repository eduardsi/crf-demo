package awsm.infrastructure.middleware;

import static java.lang.String.format;

class NoMiddlewaresException extends RuntimeException {
  NoMiddlewaresException(String cmd) {
    super(format(
            "%s has not been executed, because no middlewares are set. For production usage, set global %s.",
            cmd, Middlewares.class.getSimpleName()));
  }
}
