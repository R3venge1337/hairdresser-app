package pl.lodz.p.backend.appointment.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record CreateAppointmentForm(@NotNull UUID customerUuid, @NotNull UUID hairdresserUuid,
                                    @NotNull List<Long> hairoffers, @NotNull LocalDateTime bookedDate, @NotNull LocalDateTime finishedDate) {
}
