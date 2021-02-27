package awsm.domain.core;

@FunctionalInterface
public interface Specification<T> {
  boolean isSatisfiedBy(T entity);
}
