package lightweight4j.infra.modeling;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Optional;

import static java.util.Objects.requireNonNull;

public interface Event {

    default void schedule() {
        Publisher.publish(this);
    }

    @Component
    class Publisher {

        static Optional<ApplicationEventPublisher> GLOBAL = Optional.empty();

        static ThreadLocal<ApplicationEventPublisher> THREAD_LOCAL = new ThreadLocal<>();


        public Publisher(ApplicationEventPublisher publisher) {
            setGlobal(publisher);
        }

        public static void setGlobal(ApplicationEventPublisher publisher) {
            Publisher.GLOBAL = Optional.of(publisher);
        }

        public static void setThreadLocal(ApplicationEventPublisher publisher) {
            Publisher.THREAD_LOCAL.set(publisher);
        }

        private static void publish(Event event) {
            var publisher = Publisher.GLOBAL.orElse(THREAD_LOCAL.get());
            if (publisher == null) {
                var logger = LoggerFactory.getLogger(Publisher.class);
                logger.warn("{} has not been published. Please set global or thread local {}." ,
                        event.getClass().getSimpleName(), ApplicationEventPublisher.class.getSimpleName());
            } else {
                publisher.publishEvent(event);
            }
        }

    }

    class ThreadLocalApplicationEventPublisher implements ApplicationEventPublisher {

        private static ThreadLocal<Collection<Event>> events = new ThreadLocal<>();

        @Override
        public void publishEvent(Object event) {

        }
    }

}
