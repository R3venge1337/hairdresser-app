package pl.lodz.p.backend.appointment.domain;

import org.springframework.data.jpa.repository.JpaRepository;

interface AppointmentAccountRepository extends JpaRepository<AppointmentAccount, Long> {
    Boolean existsByUsername(final String name);

    Boolean existsByEmail(final String email);
}
