package awsm.infrastructure.middleware;

import static org.msgpack.core.Preconditions.checkState;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.stereotype.Component;

@Component
public class CommandMeta {

  private final BeanFactory spring;

  public CommandMeta(BeanFactory spring) {
    this.spring = spring;
  }

  @SuppressWarnings("ConstantConditions")
  public Class<? extends Command> typeOf(String commandId) {
    var containsBean = spring.containsBean(commandId);
    checkState(containsBean, "No bean with @CommandId [%s]", commandId);

    var beanType = spring.getType(commandId);
    checkState(Command.class.isAssignableFrom(beanType), "Bean [%s] must be of type Command", commandId);

    return beanType.asSubclass(Command.class);
  }

}
