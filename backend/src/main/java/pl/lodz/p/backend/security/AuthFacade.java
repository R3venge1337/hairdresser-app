package pl.lodz.p.backend.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import pl.lodz.p.backend.security.dto.LoginForm;
import pl.lodz.p.backend.security.dto.LoginResponse;
import pl.lodz.p.backend.security.dto.RegisterForm;

import java.io.IOException;

public interface AuthFacade {

    LoginResponse login(final LoginForm loginForm);

    void register(final RegisterForm registerForm);

    void refreshToken(HttpServletRequest request, HttpServletResponse response, Authentication auth) throws IOException;

}
