package awsm.infra.jackson;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import org.msgpack.jackson.dataformat.MessagePackFactory;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.AbstractJackson2HttpMessageConverter;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

@Configuration
class JacksonConfiguration {

  @Bean
  Jackson2ObjectMapperBuilderCustomizer customizer() {
    return jackson -> jackson
            .failOnEmptyBeans(false)
            .visibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
  }

  @Bean
  HttpMessageConverter msgPackConverter(Jackson2ObjectMapperBuilderCustomizer customizer) {
    var builder = new Jackson2ObjectMapperBuilder();
    builder.factory(new MessagePackFactory());
    builder.findModulesViaServiceLoader(true);
    customizer.customize(builder);
    var mapper = builder.build();
    return new AbstractJackson2HttpMessageConverter(mapper, MsgPack.MIME) {
    };
  }


}
