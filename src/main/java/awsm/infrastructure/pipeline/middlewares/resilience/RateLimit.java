package awsm.infrastructure.pipeline.middlewares.resilience;

import an.awesome.pipelinr.Command;
import com.google.common.reflect.TypeToken;
import io.github.bucket4j.Bandwidth;

public interface RateLimit<C extends Command<?>> {

  Bandwidth bandwidth();

  default boolean matches(C command) {
    TypeToken<C> typeToken = new TypeToken<>(getClass()) {
    };
    return typeToken.isSupertypeOf(command.getClass());
  }


}