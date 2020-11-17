package awsm.domain.core;

public interface DomainEntity<T> {

    default boolean satisfies(Specification<T> specification) {
        return specification.isSatisfiedBy((T) this);
    }

}
