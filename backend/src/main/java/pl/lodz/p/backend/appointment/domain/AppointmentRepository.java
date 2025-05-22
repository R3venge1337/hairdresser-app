package pl.lodz.p.backend.appointment.domain;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pl.lodz.p.backend.common.repository.UUIDAwareJpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

interface AppointmentRepository extends UUIDAwareJpaRepository<Appointment, Long>, JpaSpecificationExecutor<Appointment> {
    boolean existsByBookedDateAndHairdresser(LocalDateTime bookedDate, AppointmentUser hairdresser);

    @Query("SELECT ap FROM Appointment ap JOIN FETCH ap.customer c WHERE c.uuid = :userUuid")
    List<Appointment> findAllUserAppointments(@Param(value = "userUuid") UUID userUuid);

    @Query("SELECT ap FROM Appointment ap JOIN FETCH ap.customer c WHERE ap.bookedDate = :dateTime ")
    List<Appointment> findAllUsersAppointmentsByDate(@Param(value = "dateTime") LocalDateTime dateTime);
}
