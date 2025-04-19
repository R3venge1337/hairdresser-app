package pl.lodz.p.backend.security.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record RegisterForm(@NotBlank String firstname, @NotBlank String surname, @Email String email,
                           @NotBlank String username, @NotBlank String password, @NotBlank String phoneNumber) {
}
