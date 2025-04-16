package pl.lodz.p.backend.security;

import pl.lodz.p.backend.security.dto.LoginForm;
import pl.lodz.p.backend.security.dto.LoginResponse;
import pl.lodz.p.backend.security.dto.RegisterForm;

public interface AuthFacade {

    LoginResponse login(final LoginForm loginForm);

    void register(final RegisterForm registerForm);

}
