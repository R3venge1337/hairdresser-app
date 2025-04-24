package pl.lodz.p.backend.security.domain;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import pl.lodz.p.backend.security.dto.UserDto;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class JwtUtils {

    @Value("${jwt.expirationTime}")
    private long JWT_TOKEN_VALIDITY;

    @Value("${jwt.secret.key}")
    private String secret;

    static final String USER_TYPE = "userType";
    static final String USER_UUID = "userUuid";
    static final String USER_LOGIN = "userLogin";
    static final String USER_EMAIL = "userEmail";
    static final String ACCOUNT_ENABLED = "accountEnabled";
    static final String USER_FIRSTNAME = "userFirstname";
    static final String USER_SURNAME = "userSurname";
    static final String USER_PHONENUMBER = "userPhoneNumber";

    //retrieve username from jwt token
    public String getUsernameFromToken(final String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    //retrieve expiration date from jwt token
    public Date getExpirationDateFromToken(final String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    public <T> T getClaimFromToken(final String token, final Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    //for retrieveing any information from token we will need the secret key
    public Claims getAllClaimsFromToken(final String token) {
        return Jwts.parser().verifyWith(getSecretKey()).build().parseSignedClaims(token).getPayload();
    }

    private SecretKey getSecretKey() {
        final byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    //check if the token has expired
    public Boolean isTokenExpired(final String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    public String generateToken(final UserDto user) {
        final Map<String, Object> extraClaims = Map.of(
                USER_TYPE, user.role(),
                USER_LOGIN, user.username(),
                USER_UUID, user.userUuid(),
                USER_EMAIL, user.email(),
                ACCOUNT_ENABLED, user.isActive(),
                USER_FIRSTNAME, user.firstname(),
                USER_SURNAME, user.surname(),
                USER_PHONENUMBER, user.phoneNumber()
        );

        return doGenerateToken(extraClaims, user);
    }

    //while creating the token -
    //1. Define  claims of the token, like Issuer, Expiration, Subject, and the ID
    //2. Sign the JWT using the HS512 algorithm and secret key.
    //3. According to JWS Compact Serialization(https://tools.ietf.org/html/draft-ietf-jose-json-web-signature-41#section-3.1)
    //   compaction of the JWT to a URL-safe string
    private String doGenerateToken(final Map<String, Object> claims, final UserDto user) {
        return Jwts.builder()
                .claims(claims)
                .subject(user.username())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY * 1000))
                .signWith(getSecretKey())
                .compact();
    }

    //validate token
    public Boolean validateToken(final String token, final UserDetails userDetails) {
        final String username = getUsernameFromToken(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}
