package awsm.infrastructure.scheduling;

import an.awesome.pipelinr.Command;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import lombok.SneakyThrows;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import static com.fasterxml.jackson.databind.ObjectMapper.DefaultTyping.OBJECT_AND_NON_CONCRETE;

@Converter(autoApply = true)
public class CommandConverter implements AttributeConverter<Command, String> {

  private final ObjectMapper objectMapper = new ObjectMapper().activateDefaultTyping(BasicPolymorphicTypeValidator
          .builder()
          .allowIfBaseType(Command.class)
          .build(), OBJECT_AND_NON_CONCRETE);

  @SneakyThrows
  @Override
  public String convertToDatabaseColumn(Command attribute) {
    return objectMapper.writeValueAsString(attribute);
  }

  @SneakyThrows
  @Override
  public Command convertToEntityAttribute(String value) {
    return objectMapper.readValue(value, Command.class);
  }
}
