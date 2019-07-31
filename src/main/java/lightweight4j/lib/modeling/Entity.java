package lightweight4j.lib.modeling;

public interface Entity<T> {

    default boolean __(Specification<T> specification) {
        return specification.isSatisfiedBy((T) this);
    }

    default <R> R __(Function<T, R> fn) {
        return fn.appliedOn((T) this);
    }


}
