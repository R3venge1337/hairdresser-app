package pl.lodz.p.backend.appointment.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import pl.lodz.p.backend.appointment.AppointmentFacade;
import pl.lodz.p.backend.appointment.AppointmentUserFacade;
import pl.lodz.p.backend.appointment.dto.AppointmentDto;
import pl.lodz.p.backend.appointment.dto.AppointmentFilterForm;
import pl.lodz.p.backend.appointment.dto.AppointmentUserDto;
import pl.lodz.p.backend.appointment.dto.CreateAppointmentForm;
import pl.lodz.p.backend.common.controller.PageDto;
import pl.lodz.p.backend.common.controller.PageableRequest;
import pl.lodz.p.backend.common.controller.RoutePaths;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
class AppointmentController {
    private final AppointmentFacade appointmentFacade;
    private final AppointmentUserFacade appointmentUserFacade;

    @GetMapping(RoutePaths.APPOINTMENTS)
    PageDto<AppointmentDto> findAllAppointments(@Valid @RequestBody final AppointmentFilterForm filterForm, final PageableRequest pageableRequest) {
        return appointmentFacade.findAllAppointments(filterForm, pageableRequest);
    }

    @GetMapping(RoutePaths.USERS + "/" + "{uuid}" + RoutePaths.APPOINTMENTS)
    List<AppointmentDto> findAllAppointmentsSpecificUser(@PathVariable final UUID uuid) {
        return appointmentFacade.findAllAppointmentsSpecificUser(uuid);
    }

    @GetMapping(RoutePaths.APPOINTMENTS_DATE)
    List<AppointmentDto> findAllAppointmentsInSpecificDay(@RequestBody final LocalDateTime dateTime) {
        return appointmentFacade.findAllAppointmentsInSpecificDate(dateTime);
    }

    @GetMapping(RoutePaths.USERS)
    Set<AppointmentUserDto> findAllUsersByRole(@RequestParam String roleName) {
        return appointmentUserFacade.findUsersByRole(roleName);
    }

    @GetMapping(RoutePaths.APPOINTMENTS + "/{uuid}")
    AppointmentDto findAppointmentByUuid(@PathVariable final UUID uuid) {
        return appointmentFacade.findAppointmentByUuid(uuid);
    }

    @PostMapping(RoutePaths.APPOINTMENTS)
    @ResponseStatus(HttpStatus.CREATED)
    UUID makeAppointment(@Valid @RequestBody final CreateAppointmentForm createForm) {
        return appointmentFacade.makeAppointment(createForm);
    }

    @PatchMapping(RoutePaths.APPOINTMENTS + "/{uuid}/status")
    void changeAppointmentStatus(@PathVariable final UUID uuid, @RequestBody final String status) {
        appointmentFacade.changeAppointmentStatus(uuid, status);
    }

    @PatchMapping(RoutePaths.APPOINTMENTS + "/{uuid}/hairdresser/{hairdresserUuid}")
    void changeHairdresserAppointment(@PathVariable final UUID uuid, @PathVariable final UUID hairdresserUuid) {
        appointmentFacade.changeHairdresserAppointment(uuid, hairdresserUuid);
    }
}
