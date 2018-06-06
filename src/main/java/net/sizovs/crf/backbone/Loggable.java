package net.sizovs.crf.backbone;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.apache.commons.lang3.builder.ToStringStyle.SHORT_PREFIX_STYLE;

class Loggable implements Now {

    private final Logger log = LoggerFactory.getLogger(Loggable.class);

    private final Now origin;

    public Loggable(Now origin) {
        this.origin = origin;
    }

    @Override
    public <R extends Command.R, C extends Command<R>> R execute(C command) {
        log.info(">>> {}", ReflectionToStringBuilder.toString(command, SHORT_PREFIX_STYLE));
        var response = origin.execute(command);
        log.info("<<< {} ", ReflectionToStringBuilder.toString(response, SHORT_PREFIX_STYLE));
        return response;
    }
}