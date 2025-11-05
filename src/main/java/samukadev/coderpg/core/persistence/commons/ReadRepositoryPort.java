package samukadev.coderpg.core.persistence.commons;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReadRepositoryPort<T> {
    Optional<T> get(final UUID id);
    List<T> findAll();
}
