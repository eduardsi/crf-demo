package awsm.infra.middleware.impl.scheduler;

import static com.machinezoo.noexception.Exceptions.sneak;

import awsm.infra.middleware.Command;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import javax.persistence.AttributeConverter;

class CommandConverter implements AttributeConverter<Command, String> {

  private static final ObjectMapper mapper = new ObjectMapper();

  static {
    mapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
    mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
  }

  @Override
  public String convertToDatabaseColumn(Command attribute) {
    return sneak().get(() -> mapper.writeValueAsString(attribute));
  }

  @Override
  public Command convertToEntityAttribute(String command) {
    return sneak().get(() -> mapper.readValue(command, Command.class));
  }
}
