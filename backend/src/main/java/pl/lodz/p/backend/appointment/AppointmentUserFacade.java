package pl.lodz.p.backend.appointment;

import pl.lodz.p.backend.appointment.dto.AppointmentUserDto;
import pl.lodz.p.backend.appointment.dto.UserProfileDetailsDto;
import pl.lodz.p.backend.common.controller.PageDto;
import pl.lodz.p.backend.common.controller.PageableRequest;

import java.util.Set;
import java.util.UUID;

public interface AppointmentUserFacade {
    Set<AppointmentUserDto> findUsersByRole(String roleName);

    UserProfileDetailsDto getUserProfileDetailsByUuid(final UUID uuid);

    PageDto<UserProfileDetailsDto> getAllUserProfilesDetails(final PageableRequest pageableRequest);
}
