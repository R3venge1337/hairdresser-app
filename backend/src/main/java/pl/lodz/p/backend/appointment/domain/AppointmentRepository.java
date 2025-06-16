package pl.lodz.p.backend.appointment.domain;

import jakarta.annotation.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pl.lodz.p.backend.appointment.dto.AppointmentStatusCountDto;
import pl.lodz.p.backend.appointment.dto.HairOfferStatisticDto;
import pl.lodz.p.backend.common.repository.UUIDAwareJpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

interface AppointmentRepository extends UUIDAwareJpaRepository<Appointment, Long>, JpaSpecificationExecutor<Appointment> {
    boolean existsByBookedDateAndHairdresser(LocalDateTime bookedDate, AppointmentUser hairdresser);

    boolean existsByBookedDateAndHairdresserAndStatusIn(LocalDateTime bookedDate, AppointmentUser hairdresser, Set<AppointmentStatus> statuses);

    @Query(value = "SELECT ap FROM Appointment ap JOIN FETCH ap.customer c JOIN FETCH ap.hairOffers ho WHERE c.uuid = :userUuid " +
            "AND (:status IS NULL OR ap.status = :status) " +
            "AND (CAST(:bookedDateStart AS timestamp) IS NULL OR ap.bookedDate >= :bookedDateStart) " +
            "AND (CAST(:bookedDateEnd AS timestamp) IS NULL OR ap.bookedDate <= :bookedDateEnd) " +
            "AND (:hairdresserName IS NULL OR ap.hairdresser.firstname LIKE :hairdresserName) " +
            "AND (:hairdresserSurname IS NULL OR ap.hairdresser.surname LIKE :hairdresserSurname)",
            countQuery = "SELECT COUNT(ap.id) FROM Appointment ap " +
                    "WHERE ap.customer.uuid = :userUuid " +
                    "AND (:status IS NULL OR ap.status = :status) " +
                    "AND (CAST(:bookedDateStart AS timestamp) IS NULL OR ap.bookedDate >= :bookedDateStart) " +
                    "AND (CAST(:bookedDateEnd AS timestamp) IS NULL OR ap.bookedDate <= :bookedDateEnd) " +
                    "AND (:hairdresserName IS NULL OR ap.hairdresser.firstname LIKE :hairdresserName) " +
                    "AND (:hairdresserSurname IS NULL OR ap.hairdresser.surname LIKE :hairdresserSurname)")
    Page<Appointment> findAllUserAppointments(@Param(value = "userUuid") UUID userUuid,
                                              @Nullable @Param("status") AppointmentStatus status,
                                              @Nullable @Param("bookedDateStart") LocalDateTime bookedDateStart,
                                              @Nullable @Param("bookedDateEnd") LocalDateTime bookedDateEnd,
                                              @Nullable @Param("hairdresserName") String hairdresserName,
                                              @Nullable @Param("hairdresserSurname") String hairdresserSurname,
                                              Pageable pageable);

    @Query("SELECT ap FROM Appointment ap " +
            "JOIN FETCH ap.customer c " +
            "JOIN FETCH ap.hairdresser h " +
            "JOIN FETCH ap.hairOffers ho " +
            "WHERE ap.bookedDate BETWEEN :startOfDay AND :endOfDay")
    List<Appointment> findAllAppointmentsBetween(@Param("startOfDay") LocalDateTime startOfDay, @Param("endOfDay") LocalDateTime endOfDay);

    @Query("""
                SELECT a FROM Appointment a
                LEFT JOIN FETCH a.hairOffers ho
                WHERE a.hairdresser = :hairdresser
                AND a.bookedDate BETWEEN :startOfDay AND :endOfDay
                AND a.status IN :statuses
                ORDER BY a.bookedDate ASC
            """)
    List<Appointment> findByHairdresserAndBookedDateBetweenAndStatusInWithHairOffers(
            @Param("hairdresser") AppointmentUser hairdresser,
            @Param("startOfDay") LocalDateTime startOfDay,
            @Param("endOfDay") LocalDateTime endOfDay,
            @Param("statuses") Set<AppointmentStatus> statuses
    );

    @Query(value = "SELECT a.* FROM appointments a " +
            "JOIN appointments_hairoffers ah ON a.id = ah.appointment_id_fk " +
            "JOIN hair_offers ho ON ah.hairoffer_id_fk = ho.id " +
            "WHERE a.hairdresser_id_fk = :hairdresserUuid " +
            "GROUP BY a.id, a.customer_id_fk, a.hairdresser_id_fk, a.total_cost, a.status, a.booked_date " + // Musisz grupować po wszystkich polach encji, jeśli wybierasz *
            "HAVING (" +
            "    (a.booked_date + (SUM(ho.duration) * INTERVAL '1 minute')) > :requestedStart " +
            "    AND a.booked_date < :requestedEnd" +
            ")", nativeQuery = true)
    List<Appointment> findConflictingAppointments(
            @Param("hairdresserUuid") UUID hairdresserUuid,
            @Param("requestedStart") LocalDateTime requestedStart,
            @Param("requestedEnd") LocalDateTime requestedEnd);

    @Query(value = "SELECT ap FROM Appointment ap " +
            "JOIN FETCH ap.customer c " +
            "JOIN FETCH ap.hairdresser h " +
            "LEFT JOIN FETCH ap.hairOffers ho " +
            "WHERE c.uuid = :customerUuid " +
            "AND (:status IS NULL OR ap.status = :status) " +
            "AND (CAST(:bookedDateStart AS timestamp) IS NULL OR ap.bookedDate >= :bookedDateStart) " +
            "AND (CAST(:bookedDateEnd AS timestamp) IS NULL OR ap.bookedDate <= :bookedDateEnd) " +
            "AND (:hairdresserName IS NULL OR h.firstname LIKE :hairdresserName) " +
            "AND (:hairdresserSurname IS NULL OR h.surname LIKE :hairdresserSurname)",
            countQuery = "SELECT COUNT(ap.id) FROM Appointment ap " +
                    "WHERE ap.customer.uuid = :customerUuid " +
                    "AND (:status IS NULL OR ap.status = :status) " +
                    "AND (CAST(:bookedDateStart AS timestamp) IS NULL OR ap.bookedDate >= :bookedDateStart) " +
                    "AND (CAST(:bookedDateEnd AS timestamp) IS NULL OR ap.bookedDate <= :bookedDateEnd) " +
                    "AND (:hairdresserName IS NULL OR ap.hairdresser.firstname LIKE :hairdresserName) " +
                    "AND (:hairdresserSurname IS NULL OR ap.hairdresser.surname LIKE :hairdresserSurname)")
    Page<Appointment> findByCustomer_IdAndFilters(
            @Param("customerUuid") UUID customerUuid,
            @Nullable @Param("status") AppointmentStatus status,
            @Nullable @Param("bookedDateStart") LocalDateTime bookedDateStart,
            @Nullable @Param("bookedDateEnd") LocalDateTime bookedDateEnd,
            @Nullable @Param("hairdresserName") String hairdresserName,
            @Nullable @Param("hairdresserSurname") String hairdresserSurname,
            Pageable pageable);

    @EntityGraph(attributePaths = {"hairOffers", "customer", "hairdresser"})
    Page<Appointment> findAll(Specification<Appointment> spec, Pageable pageable);

    @Query("SELECT NEW pl.lodz.p.backend.appointment.dto.AppointmentStatusCountDto(ap.status, COUNT(ap.id)) " +
            "FROM Appointment ap GROUP BY ap.status")
    List<AppointmentStatusCountDto> countAppointmentsByStatusDto();

    @Query("SELECT NEW pl.lodz.p.backend.appointment.dto.HairOfferStatisticDto(ho.name, COUNT(ho.id)) " +
            "FROM Appointment ap JOIN ap.hairOffers ho " +
            "WHERE ap.status = :completedStatus OR ap.status = PAID " +
            "GROUP BY ho.name")
    List<HairOfferStatisticDto> countCompletedHairOffers(@Param("completedStatus") AppointmentStatus completedStatus);

    @Query(value = "SELECT a.* FROM appointments a " +
            "JOIN appointments_hairoffers ah ON a.id = ah.appointment_id_fk " +
            "JOIN hairoffers ho ON ah.hairoffer_id_fk = ho.id " +
            "JOIN users au ON a.hairdresser_id_fk = au.id " + // DODANO: JOIN z tabelą użytkowników
            "WHERE au.uuid = :hairdresserId " +                           // ZMIENIONO: Porównanie po UUID z tabeli użytkowników
            "  AND a.uuid != :currentAppointmentUuid " +
            "  AND a.status IN (:statuses) " +
            "GROUP BY a.id, a.customer_id_fk, a.hairdresser_id_fk, a.total_cost, a.status, a.booked_date, a.uuid, au.id, au.uuid " + // Upewnij się, że wszystkie kolumny z SELECT i JOIN są w GROUP BY
            "HAVING (" +
            "    (a.booked_date + (CAST(SUM(ho.duration) AS text) || ' minutes')::interval) > :requestedStart " +
            "    AND a.booked_date < :requestedEnd" +
            ")", nativeQuery = true)
    List<Appointment> findConflictingAppointmentsExcludingCurrentNative(
            @Param("hairdresserId") UUID hairdresserId,
            @Param("requestedStart") LocalDateTime requestedStart,
            @Param("requestedEnd") LocalDateTime requestedEnd,
            @Param("currentAppointmentUuid") UUID currentAppointmentUuid,
            @Param("statuses") Set<String> statuses
    );

    List<Appointment> findByStatusInAndFinishedAppointmentDateBefore(Set<AppointmentStatus> statuses, LocalDateTime finishedAppointmentDate);

    // NEW METHOD: Find all appointments for a specific hairdresser UUID
    @Query(value = "SELECT ap FROM Appointment ap JOIN FETCH ap.customer c JOIN FETCH ap.hairOffers ho WHERE ap.hairdresser.uuid = :hairdresserUuid " + // Changed to hairdresser UUID
            "AND (:status IS NULL OR ap.status = :status) " +
            "AND (CAST(:bookedDateStart AS timestamp) IS NULL OR ap.bookedDate >= :bookedDateStart) " +
            "AND (CAST(:bookedDateEnd AS timestamp) IS NULL OR ap.bookedDate <= :bookedDateEnd) " +
            "AND (:customerName IS NULL OR ap.customer.firstname LIKE %:customerName%) " + // Changed to customer name
            "AND (:customerSurname IS NULL OR ap.customer.surname LIKE %:customerSurname%)", // Changed to customer surname
            countQuery = "SELECT COUNT(ap.id) FROM Appointment ap " +
                    "WHERE ap.hairdresser.uuid = :hairdresserUuid " + // Changed to hairdresser UUID
                    "AND (:status IS NULL OR ap.status = :status) " +
                    "AND (CAST(:bookedDateStart AS timestamp) IS NULL OR ap.bookedDate >= :bookedDateStart) " +
                    "AND (CAST(:bookedDateEnd AS timestamp) IS NULL OR ap.bookedDate <= :bookedDateEnd) " +
                    "AND (:customerName IS NULL OR ap.customer.firstname LIKE %:customerName%) " + // Changed to customer name
                    "AND (:customerSurname IS NULL OR ap.customer.surname LIKE %:customerSurname%)") // Changed to customer surname
    Page<Appointment> findAllHairdresserAppointments(@Param(value = "hairdresserUuid") UUID hairdresserUuid, // New parameter name
                                                     @Nullable @Param("status") AppointmentStatus status,
                                                     @Nullable @Param("bookedDateStart") LocalDateTime bookedDateStart,
                                                     @Nullable @Param("bookedDateEnd") LocalDateTime bookedDateEnd,
                                                     @Nullable @Param("customerName") String customerName, // New parameter name
                                                     @Nullable @Param("customerSurname") String customerSurname, // New parameter name
                                                     Pageable pageable);

}
