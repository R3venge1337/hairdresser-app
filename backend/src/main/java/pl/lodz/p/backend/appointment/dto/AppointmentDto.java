package pl.lodz.p.backend.appointment.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

public record AppointmentDto(UUID customerUuid, String customerFirstname, String customerSurname, UUID hairDresserUuid, String hairDresserFirstname, String hairDresserSurname, BigDecimal totalCost,
                             Set<HairOfferDto> procedures, String appointmentStatus, LocalDateTime bookedAppointment) {
}
