package pl.lodz.p.backend.security.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.lodz.p.backend.common.exception.ErrorMessages;
import pl.lodz.p.backend.common.exception.NotFoundException;
import pl.lodz.p.backend.security.AppUserFacade;
import pl.lodz.p.backend.security.dto.UserDto;

@Service
@RequiredArgsConstructor
class AppUserService implements AppUserFacade {

    private final AppUserRepository appUserRepository;

    @Override
    public UserDto getUserDetailsByUsername(final String username) {
        return mapToUserDto(appUserRepository.findUserByUsername(username)
                .orElseThrow(() -> new NotFoundException(ErrorMessages.USER_NOT_FOUND)));
    }

    private UserDto mapToUserDto(final AppUser appUser) {
        return new UserDto(appUser.getUuid(), appUser.getFirstname(), appUser.getSurname(), appUser.getPhoneNumber(), appUser.getAccount().getUsername(), appUser.getAccount().getPassword(), appUser.getAccount().getEmail(), appUser.getAccount().getIsActive(), appUser.getAccount().getCreatedDate(), appUser.getAccount().getRole().getName().name());
    }
}
