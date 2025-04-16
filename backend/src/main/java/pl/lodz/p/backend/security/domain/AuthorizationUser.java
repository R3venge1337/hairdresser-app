package pl.lodz.p.backend.security.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import pl.lodz.p.backend.security.dto.UserDto;

import java.util.Collection;
import java.util.List;

@RequiredArgsConstructor
class AuthorizationUser implements UserDetails {

    @Getter
    private final UserDto userDto;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(userDto.role()));
    }

    @Override
    public String getPassword() {
        return userDto.password();
    }

    @Override
    public String getUsername() {
        return userDto.username();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return userDto.isActive();
    }
}
