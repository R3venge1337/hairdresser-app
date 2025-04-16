package pl.lodz.p.backend.security.domain;

import org.springframework.data.jpa.repository.JpaRepository;

interface AccountRepository extends JpaRepository<Account, Long> {
    Boolean existsByUsername(final String name);

    Boolean existsByEmail(final String email);
}
