package awsm.infra.middleware.impl.resilience;

import awsm.infra.middleware.Command;
import awsm.infra.middleware.Middleware;
import com.google.common.util.concurrent.RateLimiter;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ResponseStatus;

@Component
public class Throttling implements Middleware {

  private final ConcurrentHashMap<Type, RateLimiter> rateLimiters;
  private final Collection<RateLimit> rateLimits;

  public Throttling(Collection<RateLimit> rateLimits) {
    this.rateLimits = rateLimits;
    this.rateLimiters = new ConcurrentHashMap<>();
  }

  @SuppressWarnings("unchecked")
  @Override
  public <R, C extends Command<R>> R invoke(C command, Next<R> next) {

    var rateLimit = rateLimits
        .stream()
        .filter(limit -> limit.matches(command))
        .findFirst();

    rateLimit.ifPresent(limit -> throttle(command, limit));

    return next.invoke();
  }

  private <R, C extends Command<R>> void throttle(C command, RateLimit limit) {
    RateLimiter rateLimiter = rateLimiters
        .computeIfAbsent(command.getClass(), type -> RateLimiter.create(limit.rateLimit()));
    if (!rateLimiter.tryAcquire()) {
      throw new ThrottlingException();
    }
  }

  @ResponseStatus(code = HttpStatus.TOO_MANY_REQUESTS)
  private static class ThrottlingException extends RuntimeException {

  }
}
