package pl.lodz.p.backend.appointment.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileDetailsDto {
    private UUID userUuid;
    private String firstname;
    private String surname;
    private String phoneNumber;
    private String username; // Nowe pole
    private String password;
    private LocalDateTime createdDate;
    private String email;
    private Boolean isActive;
    private String name;
}
