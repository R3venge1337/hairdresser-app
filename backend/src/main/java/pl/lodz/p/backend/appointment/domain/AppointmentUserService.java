package pl.lodz.p.backend.appointment.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pl.lodz.p.backend.appointment.AppointmentUserFacade;
import pl.lodz.p.backend.appointment.dto.AppointmentUserDto;
import pl.lodz.p.backend.appointment.dto.UserProfileDetailsDto;
import pl.lodz.p.backend.common.controller.PageDto;
import pl.lodz.p.backend.common.controller.PageableRequest;
import pl.lodz.p.backend.common.controller.PageableUtils;
import pl.lodz.p.backend.common.exception.ErrorMessages;
import pl.lodz.p.backend.common.exception.NotFoundException;
import pl.lodz.p.backend.common.validation.DtoValidator;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static pl.lodz.p.backend.common.repository.PageableUtils.convertToSpringPageable;

@Service
@RequiredArgsConstructor
class AppointmentUserService implements AppointmentUserFacade {
    private final AppointmentUserRepository userRepository;

    @Override
    public Set<AppointmentUserDto> findUsersByRole(final String roleName) {
        return userRepository.findAllUsersByRole(RoleType.valueOf(roleName)).stream()
                .map(this::mapToAppointmentUserDto)
                .collect(Collectors.toSet());
    }

    @Override
    public UserProfileDetailsDto getUserProfileDetailsByUuid(final UUID uuid) {
        return userRepository.findUserProfileDetailsByUuid(uuid)
                .map(this::mapToUserProfileDetailsDto)
                .orElseThrow(() -> new NotFoundException(ErrorMessages.USER_NOT_FOUND, uuid));
    }

    private UserProfileDetailsDto mapToUserProfileDetailsDto(final AppointmentUser user) {
        return new UserProfileDetailsDto(user.getUuid(),user.getFirstname(), user.getSurname(), user.getPhoneNumber(), user.getAppointmentAccount().getUsername(), user.getAppointmentAccount().getPassword(), user.getAppointmentAccount().getCreatedDate(), user.getAppointmentAccount().getEmail(), user.getAppointmentAccount().getIsActive(), user.getAppointmentAccount().getAppointmentRole().getName().name());
    }

    @Override
    public PageDto<UserProfileDetailsDto> getAllUserProfilesDetails(final PageableRequest pageableRequest) {
        DtoValidator.validate(pageableRequest);
        Pageable pageable = convertToSpringPageable(pageableRequest);
        Page<UserProfileDetailsDto> users =  userRepository.findAllUserProfileDetails(pageable).map(this::mapToUserProfileDetailsDto);
        return PageableUtils.toDto(users);
    }

    private AppointmentUserDto mapToAppointmentUserDto(final AppointmentUser user) {
        return new AppointmentUserDto(user.getUuid(), user.getFirstname(), user.getSurname(), user.getPhoneNumber(), user.getAppointmentAccount().getUsername(), user.getAppointmentAccount().getEmail(), user.getAppointmentAccount().getIsActive(), user.getAppointmentAccount().getCreatedDate(), user.getAppointmentAccount().getAppointmentRole().getName().name());
    }
}
