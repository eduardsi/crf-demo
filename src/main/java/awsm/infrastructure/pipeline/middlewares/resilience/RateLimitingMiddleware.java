package awsm.infrastructure.pipeline.middlewares.resilience;

import an.awesome.pipelinr.Command;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(10)
@SuppressWarnings({"unchecked", "rawtypes"})
class RateLimitingMiddleware implements Command.Middleware {

  private final ConcurrentHashMap<Type, Bucket> buckets;
  private final Collection<RateLimit> rateLimits;

  public RateLimitingMiddleware(Collection<RateLimit> rateLimits) {
    this.rateLimits = rateLimits;
    this.buckets = new ConcurrentHashMap<>();
  }

  @Override
  public <R, C extends Command<R>> R invoke(C command, Next<R> next) {
    var rateLimit = rateLimits.stream().filter(limit -> limit.matches(command)).findFirst();

    if (rateLimit.isEmpty()) {
      return next.invoke();
    }

    var bandwidth = rateLimit.orElseThrow().bandwidth();
    var bucket = bucket(command, bandwidth);
    if (bucket.tryConsume(1)) {
      return next.invoke();
    } else {
      throw new RateLimitException();
    }
  }

  private <R, C extends Command<R>> Bucket bucket(C command, Bandwidth bandwidth) {
    var commandClass = command.getClass();
    return buckets.computeIfAbsent(
        commandClass, type -> Bucket4j.builder().addLimit(bandwidth).build());
  }
}
