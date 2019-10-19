package awsm.infrastructure.middleware;

import static java.util.Objects.requireNonNull;

import org.springframework.stereotype.Component;

@Component
class MiddlewaresHolder {

  private static Middlewares INSTANCE = new NoMiddlewares();

  public MiddlewaresHolder(Middlewares middlewares) {
    set(middlewares);
  }

  private static void set(Middlewares middlewares) {
    MiddlewaresHolder.INSTANCE = requireNonNull(middlewares, "Middlewares cannot be null");
  }

  static Middlewares get() {
    return INSTANCE;
  }
}
