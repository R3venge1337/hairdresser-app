package pl.lodz.p.backend.appointment.dto;


import java.time.LocalDateTime;

public record AppointmentFilterForm(String status, LocalDateTime bookedDateStart, LocalDateTime bookedDateEnd, String hairdresserName, String hairdresserSurname) {
}
