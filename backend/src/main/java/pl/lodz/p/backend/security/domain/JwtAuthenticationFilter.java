package pl.lodz.p.backend.security.domain;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import pl.lodz.p.backend.security.dto.JWTBodyAttributes;
import pl.lodz.p.backend.security.dto.UserDto;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtils jwtTokenUtil;
    private final TokenRepository tokenRepository;


    @Override
    protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response, final FilterChain filterChain)
            throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String login;

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        jwt = authHeader.substring(7);

        login = jwtTokenUtil.getUsernameFromToken(jwt);
        if (login != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            final Claims claims = jwtTokenUtil.getAllClaimsFromToken(jwt);
            final String username = claims.get(JwtUtils.USER_LOGIN, String.class);
            final String firstname = claims.get(JwtUtils.USER_FIRSTNAME, String.class);
            final String surname = claims.get(JwtUtils.USER_SURNAME, String.class);
            final String userUuid = claims.get(JwtUtils.USER_UUID, String.class);
            final String userEmail = claims.get(JwtUtils.USER_EMAIL, String.class);
            final String userRole = claims.get(JwtUtils.USER_TYPE, String.class);
            final String userPhoneNumber = claims.get(JwtUtils.USER_PHONENUMBER, String.class);
            final String accountEnabled = String.valueOf(claims.get(JwtUtils.ACCOUNT_ENABLED, Boolean.class));

            final JWTBodyAttributes attributes = new JWTBodyAttributes(userUuid, firstname, surname, userPhoneNumber, username, userEmail, accountEnabled, userRole);

            var isTokenValid = tokenRepository.findByToken(jwt)
                    .map(t -> !t.isExpired() && !t.isRevoked())
                    .orElse(false);

            if (!jwtTokenUtil.isTokenExpired(jwt) && isTokenValid) {
                final UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(createJwtBody(attributes), null, List.of(new SimpleGrantedAuthority(userRole)));

                authenticationToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }

        filterChain.doFilter(request, response);
    }

    private UserDto createJwtBody(final JWTBodyAttributes attributes) {
        return new UserDto(UUID.fromString(attributes.userUuid()), attributes.firstname(), attributes.surname(),
                attributes.phoneNumber(), attributes.username(), null, attributes.email(),
                Boolean.valueOf(attributes.isActive()), null, attributes.role());
    }
}