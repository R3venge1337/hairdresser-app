package pl.lodz.p.backend.appointment.domain;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class AppointmentScheduler {

    private final AppointmentRepository appointmentRepository;
    private final AppointmentService appointmentService; // Możesz wstrzyknąć serwis, jeśli chcesz użyć jego metod

    // Metoda uruchamiana co 5 minut (fixedRate = 300000 ms)
    // CRON expression "0 */5 * * * *" oznacza: co 5 minut, co godzinę, każdego dnia miesiąca, każdego miesiąca, każdego dnia tygodnia
    @Scheduled(fixedRate = 300000) // Co 5 minut
    //@Scheduled(cron = "0 */5 * * * *") // Alternatywna składnia CRON
    @Transactional // Upewnij się, że operacje na bazie danych są transakcyjne
    public void markCompletedAppointments() {
        LocalDateTime now = LocalDateTime.now();
        // Pobierz wszystkie spotkania, które są ACCEPTED lub RESCHEDULED
        // i których finishedAppointmentDate jest przed aktualnym czasem
        List<Appointment> appointmentsToComplete = appointmentRepository
                .findByStatusInAndFinishedAppointmentDateBefore(
                        Set.of(AppointmentStatus.ACCEPTED),
                        now
                );

        if (!appointmentsToComplete.isEmpty()) {
            System.out.println("Found " + appointmentsToComplete.size() + " appointments to mark as COMPLETED.");
            for (Appointment appointment : appointmentsToComplete) {
                appointment.setStatus(AppointmentStatus.COMPLETED);
                appointmentRepository.save(appointment);
            }
            System.out.println("Successfully marked appointments as COMPLETED.");
        } else {
            System.out.println("No appointments to mark as COMPLETED at " + now);
        }
    }
}
