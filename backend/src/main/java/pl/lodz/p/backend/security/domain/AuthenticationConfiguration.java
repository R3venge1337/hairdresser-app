package pl.lodz.p.backend.security.domain;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import pl.lodz.p.backend.security.AppUserFacade;
import pl.lodz.p.backend.security.AuthFacade;
import pl.lodz.p.backend.security.PasswordFacade;
import pl.lodz.p.backend.security.dto.UserDto;

@Configuration
class AuthenticationConfiguration {

    @Value("${jwt.secret.key}")
    private String jwtSecretKey;

    @Bean
    UserDetailsService userDetailsService(final AppUserFacade userFacade) {
        return username -> {
            final UserDto userDto = userFacade.getUserDetailsByUsername(username);
            return new AuthorizationUser(userDto);
        };
    }

    @Bean
    AuthenticationManager authenticationManager(final AppUserFacade accountFacade) {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setUserDetailsService(userDetailsService(accountFacade));
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
        return new ProviderManager(daoAuthenticationProvider);
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    JwtUtils jwtUtils() {
        return new JwtUtils();
    }

    @Bean
    AuthFacade authFacade(final AppUserFacade userFacade, final JwtUtils jwtUtils, final AuthenticationManager authenticationManager, final PasswordFacade passwordFacade, final AccountRepository accountRepository, final RoleRepository roleRepository, final AppUserRepository appUserRepository) {
        return new AuthService(userFacade, jwtUtils, authenticationManager, passwordFacade, accountRepository, roleRepository, appUserRepository);
    }

    @Bean
    PasswordFacade passwordFacade(final PasswordEncoder passwordEncoder) {
        return new PasswordService(passwordEncoder);
    }

    @Bean
    JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtUtils());
    }
}
