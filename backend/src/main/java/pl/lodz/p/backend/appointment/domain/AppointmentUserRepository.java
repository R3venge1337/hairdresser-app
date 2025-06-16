package pl.lodz.p.backend.appointment.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pl.lodz.p.backend.common.repository.UUIDAwareJpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

interface AppointmentUserRepository extends UUIDAwareJpaRepository<AppointmentUser, Long> {

    @Query("SELECT u FROM AppointmentUser u JOIN FETCH u.appointmentAccount a WHERE a.username=:username")
    Optional<AppointmentUser> findUserByUsername(@Param(value = "username") String username);

    @Query("SELECT u FROM AppointmentUser u JOIN FETCH u.appointmentAccount a JOIN FETCH a.appointmentRole r WHERE r.name=:roleName")
    Set<AppointmentUser> findAllUsersByRole(@Param(value = "roleName") RoleType roleName);

    @Query("""
            SELECT u FROM AppointmentUser u
            JOIN FETCH u.appointmentAccount acc
            JOIN FETCH acc.appointmentRole r
            WHERE u.uuid = :uuid
            """)
    Optional<AppointmentUser> findUserProfileDetailsByUuid(@Param("uuid") UUID uuid);

    @Query(value = """
        SELECT u FROM AppointmentUser u
        JOIN FETCH u.appointmentAccount acc
        JOIN FETCH acc.appointmentRole r
        """,
            countQuery = """
        SELECT count(u) FROM AppointmentUser u
        JOIN u.appointmentAccount acc
        JOIN acc.appointmentRole r
        """)
    Page<AppointmentUser> findAllUserProfileDetails(Pageable pageable);
}
