package pl.lodz.p.backend.appointment.dto;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record CreateAppointmentForm(@NotNull UUID customerUuid, @NotNull UUID hairDresserUuid, @NotNull List<Long> hairOffers, @NotNull BigDecimal totalCost, @NotNull String status,
                                    @NotNull Long duration, @NotNull LocalDateTime bookedDate) {
}
