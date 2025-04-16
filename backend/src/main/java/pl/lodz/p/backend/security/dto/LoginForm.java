package pl.lodz.p.backend.security.dto;

import jakarta.validation.constraints.NotBlank;
import pl.lodz.p.backend.common.exception.ValidationMessages;

public record LoginForm(@NotBlank(message = ValidationMessages.LOGIN) String login,
                        @NotBlank(message = ValidationMessages.PASSWORD) String password) {
}
