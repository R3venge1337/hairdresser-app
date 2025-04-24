package pl.lodz.p.backend.security.dto;

public record JWTBodyAttributes(String userUuid, String firstname, String surname, String phoneNumber, String username,
                                String email, String isActive, String role) {
}
