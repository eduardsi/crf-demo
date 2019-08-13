package awsm.infra.middleware.impl.resilience;

import awsm.infra.middleware.Command;
import awsm.infra.middleware.Middleware;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;

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

    return rateLimit
        .map(limit -> limiter(command, limit))
        .map(limiter -> limiter.limit(next::invoke))
        .orElseGet(next::invoke);

  }

  private <R, C extends Command<R>> RateLimiter limiter(C command, RateLimit limit) {
    var commandClass = command.getClass();
    return rateLimiters.computeIfAbsent(commandClass, type -> new RateLimiter(limit.rateLimit()));
  }


}
