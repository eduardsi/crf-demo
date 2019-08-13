package awsm.infra.middleware.impl.resilience;

import static java.lang.String.format;

import java.util.concurrent.Semaphore;
import java.util.function.Supplier;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

class RateLimiter {

  private final Semaphore limiter;

  RateLimiter(int limiter) {
    this.limiter = new Semaphore(limiter);
  }

  <R> R limit(Supplier<R> unlimited) {
    if (!limiter.tryAcquire()) {
      throw new ThrottlingException(limiter.availablePermits());
    }
    try {
      return unlimited.get();
    } finally {
      limiter.release();
    }
  }

  @ResponseStatus(code = HttpStatus.TOO_MANY_REQUESTS)
  private static class ThrottlingException extends RuntimeException {
    private ThrottlingException(int maxPermits) {
      super(format("Reached the maximum number of permitted requests (%s)", maxPermits));
    }
  }
}
