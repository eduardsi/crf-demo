package awsm.infrastructure.middleware;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.stereotype.Component;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Component
@SuppressWarnings("rawtypes")
class SideEffects {

  private final LoadingCache<Type, List<DomainEventListener>> cachedSideEffects;

  public SideEffects(ListableBeanFactory beanFactory) {
    this.cachedSideEffects = Caffeine.newBuilder()
            .build(key -> sideEffects(beanFactory)
                    .stream()
                    .filter(domainEventListener -> domainEventListener.eventType().isSupertypeOf(key))
                    .collect(toList()));

  }

  private Collection<DomainEventListener> sideEffects(ListableBeanFactory beanFactory) {
    return beanFactory
            .getBeansOfType(DomainEventListener.class)
            .values();
  }

  public Collection<DomainEventListener> filteredBy(DomainEvent event) {
    return cachedSideEffects.get(event.type());
  }
}