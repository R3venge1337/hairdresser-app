package pl.lodz.p.backend.appointment.dto;

import java.time.LocalTime;
import java.util.UUID;

public record AvailableSlotDto(
        LocalTime startTime,          // Czas rozpoczęcia dostępnego slotu (np. 09:00)
        LocalTime endTime,            // Czas zakończenia dostępnego slotu (np. 10:30)
        UUID hairdresserId,           // ID fryzjera, dla którego ten slot jest dostępny
        String hairdresserName,  // Imię fryzjera
        String hairdresserSurname    // Nazwisko fryzjera
) {}
