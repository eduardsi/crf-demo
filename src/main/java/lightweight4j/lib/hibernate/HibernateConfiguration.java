package lightweight4j.lib.hibernate;

import org.hibernate.EmptyInterceptor;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.Iterator;
import java.util.Map;

@Component
class HibernateConfiguration implements HibernatePropertiesCustomizer {

    private final PublishEventsAfterFlush publishEventsAfterFlush;

    public HibernateConfiguration(PublishEventsAfterFlush publishEventsAfterFlush) {
        this.publishEventsAfterFlush = publishEventsAfterFlush;
    }

    @Override
    public void customize(Map<String, Object> hibernateProperties) {
        hibernateProperties.put("hibernate.session_factory.interceptor", publishEventsAfterFlush);
    }
}


@Component
class PublishEventsAfterFlush extends EmptyInterceptor {

    private ApplicationEventPublisher eventPublisher;

    public PublishEventsAfterFlush(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    @Override
    public void postFlush(Iterator entities) {
        publishEvents(entities);
    }

    private void publishEvents(Iterator entities) {
        while (entities.hasNext()) {
            var entity = entities.next();
            if (entity instanceof HibernateEntity) {
                var domainEntity = (HibernateEntity) entity;
                domainEntity.events().forEach(eventPublisher::publishEvent);
                domainEntity.clearEvents();
            }
        }
    }

}
