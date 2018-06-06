package net.sizovs.crf.backbone;

import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicLong;

@Component
class CommandCorrelationId {

    private static final String MDC_KEY = "ccid";

    private final AtomicLong counter = new AtomicLong();

    public void storeForLogging() {
        MDC.put(MDC_KEY, next());
    }

    private String next() {
        return String.valueOf(counter.incrementAndGet() % 1000);
    }

    public void stashFromLogging() {
        MDC.remove(MDC_KEY);
    }
}