package pl.lodz.p.backend.security.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import pl.lodz.p.backend.security.AuthFacade;
import pl.lodz.p.backend.security.dto.LoginForm;
import pl.lodz.p.backend.security.dto.LoginResponse;
import pl.lodz.p.backend.security.dto.RegisterForm;

import static pl.lodz.p.backend.common.controller.RoutePaths.LOGIN;
import static pl.lodz.p.backend.common.controller.RoutePaths.REGISTRATION;

@RestController
@RequiredArgsConstructor
class AuthorizationController {
    private final AuthFacade authFacade;

    @PostMapping(LOGIN)
    LoginResponse login(@Valid @RequestBody final LoginForm loginForm) {
        return authFacade.login(loginForm);
    }

    @PostMapping(REGISTRATION)
    @ResponseStatus(HttpStatus.CREATED)
    void registerUser(@Valid @RequestBody final RegisterForm registerForm) {
        authFacade.register(registerForm);
    }
}
