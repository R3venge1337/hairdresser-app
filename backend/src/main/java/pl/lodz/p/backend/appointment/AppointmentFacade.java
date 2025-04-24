package pl.lodz.p.backend.appointment;

import pl.lodz.p.backend.appointment.dto.AppointmentDto;
import pl.lodz.p.backend.appointment.dto.AppointmentFilterForm;
import pl.lodz.p.backend.appointment.dto.CreateAppointmentForm;
import pl.lodz.p.backend.common.controller.PageDto;
import pl.lodz.p.backend.common.controller.PageableRequest;

import java.util.UUID;

public interface AppointmentFacade {
    PageDto<AppointmentDto> findAllAppointments(final AppointmentFilterForm filterForm, final PageableRequest pageableRequest);

    AppointmentDto findAppointmentByUuid(final UUID uuid);

    UUID makeAppointment(final CreateAppointmentForm createForm);

    void changeAppointmentStatus(final UUID uuid, final String status);

    void changeHairdresserAppointment(final UUID appointmentUuid, final UUID hairdresserUuid);

}
