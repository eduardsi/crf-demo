package awsm.infrastructure.middleware.scheduler;

import an.awesome.pipelinr.Command;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.typeadapters.RuntimeTypeAdapterFactory;
import javax.annotation.Nonnull;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.config.AbstractFactoryBean;
import org.springframework.stereotype.Component;

@Component
class GsonFactory extends AbstractFactoryBean<Gson> {

  private final ListableBeanFactory beanFactory;

  public GsonFactory(ListableBeanFactory beanFactory) {
    this.beanFactory = beanFactory;
  }

  @Override
  public Class<?> getObjectType() {
    return Gson.class;
  }

  @Override
  @Nonnull
  protected Gson createInstance() {
    return new GsonBuilder()
        .registerTypeAdapterFactory(typeAdapterFactory())
        .create();
  }

  private RuntimeTypeAdapterFactory<Command> typeAdapterFactory() {
    var typeAdapterFactory = RuntimeTypeAdapterFactory.of(Command.class);
    for (String commandId : scheduledCommandIds()) {
      var commandType = typeOf(commandId);
      typeAdapterFactory.registerSubtype(commandType, commandId);
    }
    return typeAdapterFactory;
  }

  private String[] scheduledCommandIds() {
    return beanFactory.getBeanNamesForAnnotation(ScheduledCommandId.class);
  }

  @SuppressWarnings("ConstantConditions")
  private Class<? extends Command> typeOf(String commandId) {
    return beanFactory.getType(commandId).asSubclass(Command.class);
  }

}
