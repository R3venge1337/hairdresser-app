package pl.lodz.p.backend.appointment.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

interface AppointmentRoleRepository extends JpaRepository<AppointmentRole, Long> {
    Optional<AppointmentRole> findByName(RoleType name);
}
