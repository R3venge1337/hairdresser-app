package pl.lodz.p.backend.appointment.dto;

import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;
import pl.lodz.p.backend.common.exception.ErrorMessages;

import java.time.LocalDateTime;

public record RescheduleAppointmentForm(@NotNull(message = ErrorMessages.APPOINTMENT_RESCHEDULED_DATE_NOT_FOUD) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime newBookedDate) {
}
