package pl.lodz.p.backend.appointment;

import org.springframework.security.core.Authentication;
import pl.lodz.p.backend.appointment.dto.AppointmentDto;
import pl.lodz.p.backend.appointment.dto.AppointmentFilterForm;
import pl.lodz.p.backend.appointment.dto.AppointmentStatusCountDto;
import pl.lodz.p.backend.appointment.dto.AvailableSlotDto;
import pl.lodz.p.backend.appointment.dto.CreateAppointmentForm;
import pl.lodz.p.backend.appointment.dto.HairOfferStatisticDto;
import pl.lodz.p.backend.appointment.dto.HairdresserDto;
import pl.lodz.p.backend.appointment.dto.RescheduleAppointmentForm;
import pl.lodz.p.backend.common.controller.PageDto;
import pl.lodz.p.backend.common.controller.PageableRequest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface AppointmentFacade {
    PageDto<AppointmentDto> findAllAppointments(final AppointmentFilterForm filterForm, final PageableRequest pageableRequest);

    PageDto<AppointmentDto> findAllAppointmentsSpecificUser(final UUID uuid,final AppointmentFilterForm filterForm, final PageableRequest pageableRequest);

    List<AppointmentDto> findAllAppointmentsInSpecificDate(final LocalDate date);

    AppointmentDto findAppointmentByUuid(final UUID uuid);

    UUID makeAppointment(final CreateAppointmentForm createForm);

    void changeAppointmentStatus(final UUID uuid, final String status);

    void changeHairdresserAppointment(final UUID appointmentUuid, final UUID hairdresserUuid);

    boolean isHairdresserAvailable(final UUID hairdresserId, final LocalDateTime startDate, final Long totalDurations);

    Set<HairdresserDto> getAllAvailableHairdressers(final LocalDateTime startDate, final Long totalDurations);

    PageDto<AppointmentDto> findAllClientAppointments(final Authentication authentication, final AppointmentFilterForm filterForm, final PageableRequest pageableRequest);

    List<AvailableSlotDto> generateAvailableSlot(final LocalDate selectedDate, final Long requiredDurationMinutes,Authentication authentication);

    List<AppointmentStatusCountDto> getStatisticDataAppointmentsByStatus();

    List<HairOfferStatisticDto> getStatisticCompletedHairOfferCounts();

    void rescheduleAppointment(UUID uuid, RescheduleAppointmentForm form);

    PageDto<AppointmentDto> findAllAppointmentsWhereHairdresserIsCommon(final UUID uuid,final AppointmentFilterForm filterForm, final PageableRequest pageableRequest);
}
