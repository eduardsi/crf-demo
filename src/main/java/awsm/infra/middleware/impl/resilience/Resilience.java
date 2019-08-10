package awsm.infra.middleware.impl.resilience;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;
import net.jodah.failsafe.Failsafe;
import net.jodah.failsafe.Policy;

public class Resilience<R> {

  private final List<Policy<R>> policies;

  @SafeVarargs
  public Resilience(Policy<R>... policy) {
    this.policies = Arrays.asList(policy);
  }

  public R wrapIfNecessary(Supplier<R> supplier) {
    if (policies.isEmpty()) {
      return supplier.get();
    } else {
      return Failsafe.with(policies).get(supplier::get);
    }

  }
}
