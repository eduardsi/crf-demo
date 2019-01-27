package net.sizovs.crf.backbone;

import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicLong;

@Component
class Ccid {

    private static final String MDC_KEY = "ccid";

    private final AtomicLong counter = new AtomicLong();

    public MDC.MDCCloseable storeForLogging() {
        return MDC.putCloseable(MDC_KEY, next());
    }

    private String next() {
        return String.valueOf(counter.incrementAndGet() % 1000);
    }

}