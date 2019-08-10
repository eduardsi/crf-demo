package awsm.domain;

public interface DomainEntity<T> {

  @SuppressWarnings("unchecked")
  default boolean __(Specification<T> specification) {
    var entity = (T) this;
    return specification.isSatisfiedBy(entity);
  }

  @SuppressWarnings("unchecked")
  default <R> R __(Function<T, R> function) {
    var e = (T) this;
    return function.appliedOn(e);
  }

  interface Function<T, R> {
    R appliedOn(T entity);
  }

  interface Specification<T> {
    boolean isSatisfiedBy(T entity);
  }

}
