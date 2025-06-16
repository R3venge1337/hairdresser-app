package pl.lodz.p.backend.appointment.controller;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.hibernate.query.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
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
import pl.lodz.p.backend.appointment.dto.AppointmentStatusCountDto;
import pl.lodz.p.backend.appointment.dto.AppointmentUserDto;
import pl.lodz.p.backend.appointment.dto.AvailableSlotDto;
import pl.lodz.p.backend.appointment.dto.CreateAppointmentForm;
import pl.lodz.p.backend.appointment.dto.HairOfferStatisticDto;
import pl.lodz.p.backend.appointment.dto.HairdresserDto;
import pl.lodz.p.backend.appointment.dto.HairdressersAppointmentChecker;
import pl.lodz.p.backend.appointment.dto.RescheduleAppointmentForm;
import pl.lodz.p.backend.appointment.dto.StatusDto;
import pl.lodz.p.backend.common.controller.PageDto;
import pl.lodz.p.backend.common.controller.PageableRequest;
import pl.lodz.p.backend.common.controller.RoutePaths;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
class AppointmentController {
    private final AppointmentFacade appointmentFacade;
    private final AppointmentUserFacade appointmentUserFacade;

    @PostMapping(RoutePaths.APPOINTMENTS + "/all")
    PageDto<AppointmentDto> findAllAppointments(@Valid @RequestBody final AppointmentFilterForm filterForm, final PageableRequest pageableRequest) {
        return appointmentFacade.findAllAppointments(filterForm, pageableRequest);
    }

    @GetMapping(RoutePaths.APPOINTMENTS_SLOTS)
    public List<AvailableSlotDto> getAvailableSlots(
            @RequestParam("date") String dateString,
            @RequestParam("duration") Long durationMinutes,
            Authentication authentication
    ) {
        LocalDate date = LocalDate.parse(dateString);
        return appointmentFacade.generateAvailableSlot(date, durationMinutes,authentication);
    }

    @PostMapping(RoutePaths.USERS + "/" + "{uuid}" + RoutePaths.APPOINTMENTS)
    PageDto<AppointmentDto> findAllAppointmentsSpecificUser(@PathVariable final UUID uuid, @Valid @RequestBody final AppointmentFilterForm filterForm, final PageableRequest pageableRequest) {
        return appointmentFacade.findAllAppointmentsSpecificUser(uuid,filterForm,pageableRequest);
    }

    @GetMapping(RoutePaths.APPOINTMENTS_DATE)
    List<AppointmentDto> findAllAppointmentsInSpecificDay(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) final LocalDate date) {
        return appointmentFacade.findAllAppointmentsInSpecificDate(date);
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
    @Transactional
    UUID makeAppointment(@Valid @RequestBody final CreateAppointmentForm createForm) {
        return appointmentFacade.makeAppointment(createForm);
    }

    @PostMapping(RoutePaths.HAIRDRESSER_AVAILABLE)
    Set<HairdresserDto> getAvailableHairdressers(@Valid @RequestBody final HairdressersAppointmentChecker checker) {
        return appointmentFacade.getAllAvailableHairdressers(checker.startDate(), checker.totalDurations());
    }

    @PatchMapping(RoutePaths.APPOINTMENTS + "/{uuid}/change-status")
    void changeAppointmentStatus(@PathVariable final UUID uuid, @RequestBody final StatusDto status) {
        appointmentFacade.changeAppointmentStatus(uuid, status.statusValue());
    }

    @PatchMapping(RoutePaths.APPOINTMENTS + "/{uuid}/hairdresser/{hairdresserUuid}")
    void changeHairdresserAppointment(@PathVariable final UUID uuid, @PathVariable final UUID hairdresserUuid) {
        appointmentFacade.changeHairdresserAppointment(uuid, hairdresserUuid);
    }

    @PostMapping(RoutePaths.MY_APPOINTMENTS)
    PageDto<AppointmentDto> findAllClientAppointments(final Authentication authentication, @Valid @RequestBody final AppointmentFilterForm filterForm, final PageableRequest pageableRequest) {
        return appointmentFacade.findAllClientAppointments(authentication, filterForm, pageableRequest);
    }

    @GetMapping(RoutePaths.APPOINTMENTS + "/statistics-by-status")
    List<AppointmentStatusCountDto> getStatisticForAppointmentsByStatus() {
        return appointmentFacade.getStatisticDataAppointmentsByStatus();
    }

    @GetMapping("/statistics/statistics-by-hairoffer-name")
    List<HairOfferStatisticDto> getStatisticCompletedHairOfferCounts() {
        return appointmentFacade.getStatisticCompletedHairOfferCounts();
    }

    @PatchMapping(RoutePaths.APPOINTMENTS + "/{uuid}/reschedule")
    void rescheduleAppointment(@PathVariable final UUID uuid, @RequestBody final RescheduleAppointmentForm form) {
        appointmentFacade.rescheduleAppointment(uuid, form);
    }

    @PostMapping(RoutePaths.USERS + "/" + "{uuid}" +"/hairdresser-appointments")
    PageDto<AppointmentDto> findAllAppointmentsWhereHairdresserIsCommon(@PathVariable final UUID uuid, @Valid @RequestBody final AppointmentFilterForm filterForm, final PageableRequest pageableRequest) {
        return appointmentFacade.findAllAppointmentsWhereHairdresserIsCommon(uuid,filterForm,pageableRequest);
    }
}
