package awsm.infra.middleware.impl.resilience;

public interface RateLimited {

  int maxPerSecond();

}
