package awsm.infra.middleware.impl.resilience;

import awsm.infra.middleware.Command;
import awsm.infra.middleware.Middleware;
import com.google.common.util.concurrent.RateLimiter;
import java.lang.reflect.Type;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ResponseStatus;

@Component
public class RateLimit implements Middleware {

  private final ConcurrentHashMap<Type, RateLimiter> rateLimiters = new ConcurrentHashMap<>();

  @Override
  public <R, C extends Command<R>> R invoke(C command, Next<R> next) {
    if (!(command instanceof RateLimited)) {
      return next.invoke();
    }

    var rateLimited = (RateLimited) command;
    RateLimiter rateLimiter = rateLimiters
            .computeIfAbsent(command.getClass(), type -> RateLimiter.create(rateLimited.maxPerSecond()));

    if (!rateLimiter.tryAcquire()) {
      throw new ThrottlingException();
    }

    return next.invoke();
  }


  @ResponseStatus(code = HttpStatus.TOO_MANY_REQUESTS)
  private static class ThrottlingException extends RuntimeException {

  }
}
