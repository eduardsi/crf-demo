package awsm.infra.middleware.impl.scheduler;

import static org.zalando.fauxpas.FauxPas.throwingSupplier;

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
    return throwingSupplier(() -> mapper.writeValueAsString(attribute)).get();
  }

  @Override
  public Command convertToEntityAttribute(String command) {
    return throwingSupplier(() -> mapper.readValue(command, Command.class)).get();
  }
}
