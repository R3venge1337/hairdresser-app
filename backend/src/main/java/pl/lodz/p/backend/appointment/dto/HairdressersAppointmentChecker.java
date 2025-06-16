package pl.lodz.p.backend.appointment.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record HairdressersAppointmentChecker(@NotNull LocalDateTime startDate, @NotNull Long totalDurations) {
}
