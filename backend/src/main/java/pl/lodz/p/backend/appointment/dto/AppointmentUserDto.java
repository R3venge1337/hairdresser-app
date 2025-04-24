package pl.lodz.p.backend.appointment.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record AppointmentUserDto(UUID userUuid, String firstname, String surname, String phoneNumber, String username,
                                 String email, Boolean isActive, LocalDateTime createdDate,
                                 String role) {
}
