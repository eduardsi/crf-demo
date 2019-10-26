package awsm.infrastructure.middleware.impl.execution;

import com.google.common.collect.ForwardingCollection;
import java.util.Collection;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.stereotype.Component;

@Component
class Executors extends ForwardingCollection<Executor> {

  private final ListableBeanFactory beanFactory;

  public Executors(ListableBeanFactory beanFactory) {
    this.beanFactory = beanFactory;
  }

  @Override
  protected Collection<Executor> delegate() {
    return beanFactory.getBeansOfType(Executor.class).values();
  }
}
