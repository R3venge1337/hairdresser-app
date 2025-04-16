package pl.lodz.p.backend.security.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.lodz.p.backend.security.PasswordFacade;

@Service
@RequiredArgsConstructor
class PasswordService implements PasswordFacade {

    private final PasswordEncoder passwordEncoder;

    @Override
    public String encodePassword(final String password) {
        return passwordEncoder.encode(password);
    }

    @Override
    public Boolean matchPassword(final String raw, final String encoded) {
        return passwordEncoder.matches(raw, encoded);
    }
}