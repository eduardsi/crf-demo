package awsm.infrastructure.modeling;

import java.util.function.Function;
import java.util.function.Predicate;

public interface DomainEntity<T> {

  @SuppressWarnings("unchecked")
  default boolean testIf(Predicate<T> specification) {
    var entity = (T) this;
    return specification.test(entity);
  }

  @SuppressWarnings("unchecked")
  default <R> R as(Function<T, R> function) {
    var e = (T) this;
    return function.apply(e);
  }

}
