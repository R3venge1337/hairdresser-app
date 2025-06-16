package pl.lodz.p.backend.appointment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record UpdateHairOfferForm(@NotBlank String name, @NotBlank String description, @NotNull BigDecimal price, @NotNull Long duration) {
}
