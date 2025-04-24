package pl.lodz.p.backend.appointment.domain;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pl.lodz.p.backend.common.repository.UUIDAwareJpaRepository;

import java.util.Optional;
import java.util.Set;

interface AppointmentUserRepository extends UUIDAwareJpaRepository<AppointmentUser, Long> {

    @Query("SELECT u FROM AppointmentUser u JOIN FETCH u.appointmentAccount a WHERE a.username=:username")
    Optional<AppointmentUser> findUserByUsername(@Param(value = "username") String username);

    @Query("SELECT u FROM AppointmentUser u JOIN FETCH u.appointmentAccount a JOIN FETCH a.appointmentRole r WHERE r.name=:roleName")
    Set<AppointmentUser> findAllUsersByRole(@Param(value = "roleName") RoleType roleName);
}
