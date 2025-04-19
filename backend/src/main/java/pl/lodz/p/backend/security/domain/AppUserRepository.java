package pl.lodz.p.backend.security.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

interface AppUserRepository extends JpaRepository<AppUser, Long> {

    @Query("SELECT u FROM AppUser u JOIN FETCH u.account a WHERE a.username=:username")
    Optional<AppUser> findUserByUsername(@Param(value = "username") String username);
}
