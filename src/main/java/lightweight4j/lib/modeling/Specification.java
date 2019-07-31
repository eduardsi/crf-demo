package lightweight4j.lib.modeling;

public interface Specification<T> {

    boolean isSatisfiedBy(T entity);

}
