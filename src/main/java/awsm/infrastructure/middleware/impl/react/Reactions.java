package awsm.infrastructure.middleware.impl.react;

import com.google.common.collect.ForwardingCollection;
import java.util.Collection;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.stereotype.Component;

@Component
class Reactions extends ForwardingCollection<Reaction> {

  private ListableBeanFactory beanFactory;

  public Reactions(ListableBeanFactory beanFactory) {
    this.beanFactory = beanFactory;
  }

  @Override
  protected Collection<Reaction> delegate() {
    return beanFactory.getBeansOfType(Reaction.class).values();
  }
}
