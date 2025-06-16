package pl.lodz.p.backend.appointment.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.lodz.p.backend.appointment.AppointmentUserFacade;
import pl.lodz.p.backend.appointment.dto.UserProfileDetailsDto;
import pl.lodz.p.backend.common.controller.PageDto;
import pl.lodz.p.backend.common.controller.PageableRequest;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
class UserController {

    private final AppointmentUserFacade userFacade;

    @GetMapping("/my-profile")
    public UserProfileDetailsDto getUserProfileDetails(@RequestParam UUID uuid) {
        return userFacade.getUserProfileDetailsByUuid(uuid);

    }

    @GetMapping("/all-profiles")
    public PageDto<UserProfileDetailsDto> getAllUserProfiles(final PageableRequest pageableRequest) {
        return userFacade.getAllUserProfilesDetails(pageableRequest);
    }
}
