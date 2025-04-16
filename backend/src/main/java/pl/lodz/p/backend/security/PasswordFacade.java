package pl.lodz.p.backend.security;

public interface PasswordFacade {

  String encodePassword(final String password);

  Boolean matchPassword(final String raw, final String encoded);
}