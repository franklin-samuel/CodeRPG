package samukadev.coderpg.core.persistence.commons;

public interface WriteRepositoryPort<T> {
    T save(final T model);
}
