package awsm.infra.pipeline.logging;

import static org.zalando.fauxpas.TryWith.tryWith;

import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Supplier;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

@Component
class CorrelationId {

  private static final String MDC_KEY = "ccid";

  private final AtomicLong counter = new AtomicLong();

  <T> T wrap(Supplier<T> action) {
    var closeable = MDC.putCloseable(MDC_KEY, next());
    return tryWith(closeable, any -> {
      return action.get();
    });
  }

  private String next() {
    return String.valueOf(counter.incrementAndGet() % 1000);
  }

}