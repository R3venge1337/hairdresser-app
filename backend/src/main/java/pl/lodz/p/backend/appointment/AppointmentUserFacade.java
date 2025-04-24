package pl.lodz.p.backend.appointment;

import pl.lodz.p.backend.appointment.dto.AppointmentUserDto;

import java.util.Set;

public interface AppointmentUserFacade {
    Set<AppointmentUserDto> findUsersByRole(String roleName);
}
