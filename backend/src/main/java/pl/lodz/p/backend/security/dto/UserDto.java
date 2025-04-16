package pl.lodz.p.backend.security.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserDto(UUID userUuid, String firstname, String surname, String phoneNumber, String username,
                      String password, String email, Boolean isActive, LocalDateTime createdDate, String role) {
}
