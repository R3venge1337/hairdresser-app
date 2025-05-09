package pl.lodz.p.backend.common.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;
import pl.lodz.p.backend.common.domain.AbstractUuidEntity;

import java.util.Optional;
import java.util.UUID;

@NoRepositoryBean
public interface UUIDAwareJpaRepository<T extends AbstractUuidEntity, ID> extends JpaRepository<T, ID> {
    Optional<T> findByUuid(final UUID uuid);

    boolean existsByUuid(final UUID uuid);

    void deleteByUuid(final UUID uuid);
}