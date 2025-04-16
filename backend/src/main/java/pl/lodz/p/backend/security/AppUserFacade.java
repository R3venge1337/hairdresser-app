package pl.lodz.p.backend.security;

import pl.lodz.p.backend.security.dto.UserDto;

public interface AppUserFacade {

    UserDto getUserDetailsByUsername(final String username);
}
