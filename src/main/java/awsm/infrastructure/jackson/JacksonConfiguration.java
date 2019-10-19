package awsm.infrastructure.jackson;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class JacksonConfiguration {

  @Bean
  Jackson2ObjectMapperBuilderCustomizer customizer() {
    return jackson -> jackson
            .failOnEmptyBeans(false)
            .visibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
  }


}
