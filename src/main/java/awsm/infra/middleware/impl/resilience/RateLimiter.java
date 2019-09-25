package awsm.infra.middleware.impl.resilience;

import static java.lang.String.format;

import java.util.concurrent.Semaphore;
import java.util.function.Supplier;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

class RateLimiter {

  private final int limit;
  private final Semaphore limiter;

  RateLimiter(int limit) {
    this.limiter = new Semaphore(limit);
    this.limit = limit;
  }

  <R> R limit(Supplier<R> unlimited) {
    if (!limiter.tryAcquire()) {
      throw new ThrottlingException(limit);
    }
    try {
      return unlimited.get();
    } finally {
      limiter.release();
    }
  }

  @ResponseStatus(code = HttpStatus.TOO_MANY_REQUESTS)
  static class ThrottlingException extends RuntimeException {
    private ThrottlingException(int maxPermits) {
      super(format("Reached the maximum number of permitted concurrent requests (%s)", maxPermits));
    }
  }
}
