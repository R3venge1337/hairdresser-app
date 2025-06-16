package pl.lodz.p.backend.security.dto;

public record LoginResponse(String accessToken, String refreshToken) {
}
