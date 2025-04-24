package pl.lodz.p.backend.appointment.domain;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import pl.lodz.p.backend.common.repository.UUIDAwareJpaRepository;

import java.time.LocalDateTime;

interface AppointmentRepository extends UUIDAwareJpaRepository<Appointment, Long>, JpaSpecificationExecutor<Appointment> {
    boolean existsByBookedDateAndHairdresser(LocalDateTime bookedDate, AppointmentUser  hairdresser);
}
