package pl.lodz.p.backend.appointment.dto;


import pl.lodz.p.backend.appointment.domain.AppointmentStatus;

public record AppointmentStatusCountDto(AppointmentStatus status,
                                        Long count) {
}
