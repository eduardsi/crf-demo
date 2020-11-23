package awsm.infrastructure.pipeline.ratelimit;

import an.awesome.pipelinr.Command;
import com.github.bucket4j.Bandwidth;
import com.github.bucket4j.Bucket;
import com.github.bucket4j.Bucket4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.Type;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Order(2)
class RateLimiting implements Command.Middleware {

  private final ConcurrentHashMap<Type, Bucket> buckets;

  public RateLimiting() {
    this.buckets = new ConcurrentHashMap<>();
  }

  @Override
  public <R, C extends Command<R>> R invoke(C command, Next<R> next) {

    if (!(command instanceof RateLimited)) {
      return next.invoke();
    }

    var rateLimited = (RateLimited) command;
    var bandwidth = rateLimited.bandwidth();

    var bucket = bucket(command, bandwidth);
    if (bucket.tryConsumeSingleToken()) {
      return next.invoke();
    } else {
      throw new RateLimitException();
    }
  }

  private <R, C extends Command<R>> Bucket bucket(C command, Bandwidth bandwidth) {
    var commandClass = command.getClass();
    return buckets.computeIfAbsent(commandClass, type ->  Bucket4j.builder().addLimit(bandwidth).build());
  }


}