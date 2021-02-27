package awsm.infrastructure.scheduling;

import static java.util.Arrays.asList;

import an.awesome.pipelinr.Command;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.typeadapters.RuntimeTypeAdapterFactory;
import java.util.List;
import javax.annotation.Nonnull;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.config.AbstractFactoryBean;
import org.springframework.stereotype.Component;

@Component
class GsonBeanFactory extends AbstractFactoryBean<Gson> {

  private final ListableBeanFactory beanFactory;
  private final RuntimeTypeAdapterFactory<Command> adapter;

  public GsonBeanFactory(ListableBeanFactory beanFactory) {
    this.beanFactory = beanFactory;
    this.adapter = RuntimeTypeAdapterFactory.of(Command.class);
    commandIds().forEach(this::bindToType);
  }

  private List<String> commandIds() {
    return asList(beanFactory.getBeanNamesForAnnotation(ScheduledCommandId.class));
  }

  private void bindToType(String commandId) {
    var type = beanFactory.getType(commandId).asSubclass(Command.class);
    adapter.registerSubtype(type, commandId);
  }

  @Override
  @Nonnull
  protected Gson createInstance() {
    return new GsonBuilder().registerTypeAdapterFactory(adapter).create();
  }

  @Override
  public Class<?> getObjectType() {
    return Gson.class;
  }
}
