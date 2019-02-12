package lightweight4j.lib.pipeline;

import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicLong;

@Component
class CorrelationId {

    private static final String MDC_KEY = "ccid";

    private final AtomicLong counter = new AtomicLong();

    public MDC.MDCCloseable storeForLogging() {
        return MDC.putCloseable(MDC_KEY, next());
    }

    private String next() {
        return String.valueOf(counter.incrementAndGet() % 1000);
    }

}