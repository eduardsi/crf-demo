package lightweight4j.lib.modeling;

public interface Entity<T> {

    @SuppressWarnings("unchecked")
    default boolean __(Specification<T> specification) {
        var entity = (T) this;
        return specification.isSatisfiedBy(entity);
    }

    @SuppressWarnings("unchecked")
    default <R> R __(Function<T, R> fn) {
        var e = (T) this;
        return fn.appliedOn(e);
    }


}
