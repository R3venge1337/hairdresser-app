package pl.lodz.p.backend.appointment.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.lodz.p.backend.appointment.AppointmentUserFacade;
import pl.lodz.p.backend.appointment.dto.AppointmentUserDto;

import java.util.Set;
import java.util.stream.Collectors;

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

    private AppointmentUserDto mapToAppointmentUserDto(final AppointmentUser user) {
        return new AppointmentUserDto(user.getUuid(), user.getFirstname(), user.getSurname(), user.getPhoneNumber(), user.getAppointmentAccount().getUsername(), user.getAppointmentAccount().getEmail(), user.getAppointmentAccount().getIsActive(), user.getAppointmentAccount().getCreatedDate(), user.getAppointmentAccount().getAppointmentRole().getName().name());
    }
}
