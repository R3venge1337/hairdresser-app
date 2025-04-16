package pl.lodz.p.backend.security.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import pl.lodz.p.backend.common.exception.AlreadyExistException;
import pl.lodz.p.backend.common.exception.ErrorMessages;
import pl.lodz.p.backend.common.validation.DtoValidator;
import pl.lodz.p.backend.security.AppUserFacade;
import pl.lodz.p.backend.security.AuthFacade;
import pl.lodz.p.backend.security.PasswordFacade;
import pl.lodz.p.backend.security.dto.LoginForm;
import pl.lodz.p.backend.security.dto.LoginResponse;
import pl.lodz.p.backend.security.dto.RegisterForm;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
class AuthService implements AuthFacade {

    private final AppUserFacade userFacade;
    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;
    private final PasswordFacade passwordFacade;
    private final AccountRepository accountRepository;
    private final RoleRepository roleRepository;
    private final AppUserRepository appUserRepository;

    @Override
    public LoginResponse login(final LoginForm loginForm) {
        DtoValidator.validate(loginForm);
        Authentication auth = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginForm.login(), loginForm.password()));
        AuthorizationUser authorizationUser = (AuthorizationUser) auth.getPrincipal();
        String jwt = jwtUtils.generateToken(authorizationUser.getUserDto());
        return new LoginResponse(jwt);
    }

    @Override
    public void register(final RegisterForm registerForm) {
        DtoValidator.validate(registerForm);
        if (accountRepository.existsByEmail(registerForm.email())) {
            throw new AlreadyExistException(ErrorMessages.USER_EXIST_BY_EMAIL);
        }
        if (accountRepository.existsByUsername(registerForm.username())) {
            throw new AlreadyExistException(ErrorMessages.USER_EXIST_BY_NAME);
        }
        Role role = roleRepository.findByName(RoleType.CUSTOMER).orElse(null);
        Account account = new Account();
        account.setEmail(registerForm.email());
        account.setUsername(registerForm.username());
        account.setPassword(passwordFacade.encodePassword(registerForm.password()));
        account.setIsActive(true);
        account.setCreatedDate(LocalDateTime.now());
        account.setRole(role);

        Account accountEntity = accountRepository.save(account);

        AppUser user = new AppUser();
        user.setFirstname(registerForm.firstname());
        user.setSurname(registerForm.surname());
        user.setPhoneNumber(registerForm.phoneNumber());
        user.setAccount(accountEntity);

        appUserRepository.save(user);

    }
}
