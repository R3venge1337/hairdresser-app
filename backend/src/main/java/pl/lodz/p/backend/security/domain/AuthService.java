package pl.lodz.p.backend.security.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import pl.lodz.p.backend.common.exception.AlreadyExistException;
import pl.lodz.p.backend.common.exception.ErrorMessages;
import pl.lodz.p.backend.common.exception.NotFoundException;
import pl.lodz.p.backend.common.validation.DtoValidator;
import pl.lodz.p.backend.security.AuthFacade;
import pl.lodz.p.backend.security.PasswordFacade;
import pl.lodz.p.backend.security.dto.LoginForm;
import pl.lodz.p.backend.security.dto.LoginResponse;
import pl.lodz.p.backend.security.dto.RefreshTokenResponse;
import pl.lodz.p.backend.security.dto.RegisterForm;
import pl.lodz.p.backend.security.dto.UserDto;

import java.io.IOException;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
class AuthService implements AuthFacade {

    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;
    private final PasswordFacade passwordFacade;
    private final AccountRepository accountRepository;
    private final RoleRepository roleRepository;
    private final AppUserRepository appUserRepository;
    private final TokenRepository tokenRepository;

    @Override
    public LoginResponse login(final LoginForm loginForm) {
        DtoValidator.validate(loginForm);
        Authentication auth = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginForm.username(), loginForm.password()));
        AuthorizationUser authorizationUser = (AuthorizationUser) auth.getPrincipal();
        String jwt = jwtUtils.generateToken(authorizationUser.getUserDto());
        String refreshJwt = jwtUtils.generateRefreshToken(authorizationUser.getUserDto());
        AppUser retrivedUserDetails = appUserRepository.findUserByUsername(authorizationUser.getUsername())
                .orElseThrow(() -> new NotFoundException(ErrorMessages.USER_NOT_FOUND, authorizationUser.getUserDto().userUuid()));
        revokeAllUserTokens(retrivedUserDetails);
        saveUserToken(retrivedUserDetails, jwt);
        return new LoginResponse(jwt, refreshJwt);
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
        final Role role = roleRepository.findByName(RoleType.CUSTOMER).orElse(null);
        final Account account = createAccount(registerForm, role);
        final Account accountEntity = accountRepository.save(account);
        final AppUser user = createAppUser(registerForm, accountEntity);

        appUserRepository.save(user);

    }

    private static AppUser createAppUser(final RegisterForm registerForm, final Account accountEntity) {
        AppUser user = new AppUser();
        user.setFirstname(registerForm.firstname());
        user.setSurname(registerForm.surname());
        user.setPhoneNumber(registerForm.phoneNumber());
        user.setAccount(accountEntity);
        return user;
    }

    private Account createAccount(final RegisterForm registerForm, final Role role) {
        Account account = new Account();
        account.setEmail(registerForm.email());
        account.setUsername(registerForm.username());
        account.setPassword(passwordFacade.encodePassword(registerForm.password()));
        account.setIsActive(true);
        account.setCreatedDate(LocalDateTime.now());
        account.setRole(role);
        return account;
    }

    private void saveUserToken(AppUser user, String jwtToken) {
        var token = Token.builder()
                .user(user)
                .token(jwtToken)
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();
        tokenRepository.save(token);
    }

    private void revokeAllUserTokens(AppUser user) {
        var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
        if (validUserTokens.isEmpty())
            return;
        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        tokenRepository.saveAll(validUserTokens);
    }

    @Override
    public void refreshToken(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication auth
    ) throws IOException {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String refreshToken;
        final String username;

        UserDto userDto = (UserDto) auth.getPrincipal();
        if (authHeader == null ||!authHeader.startsWith("Bearer ")) {
            return;
        }
        refreshToken = authHeader.substring(7);
        username = jwtUtils.getUsernameFromToken(refreshToken);
        if (username != null) {
            AppUser user = this.appUserRepository.findUserByUsername(username)
                    .orElseThrow();
           AuthorizationUser authorizationUser = new AuthorizationUser(userDto);
            if (jwtUtils.validateToken(refreshToken,authorizationUser)) {
                var accessToken = jwtUtils.generateRefreshToken(authorizationUser.getUserDto());
                revokeAllUserTokens(user);
                saveUserToken(user, accessToken);
                var authResponse = new RefreshTokenResponse(accessToken);
                new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
            }
        }
    }
}
