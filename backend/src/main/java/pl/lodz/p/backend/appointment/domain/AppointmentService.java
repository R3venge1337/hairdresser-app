package pl.lodz.p.backend.appointment.domain;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import pl.lodz.p.backend.appointment.AppointmentFacade;
import pl.lodz.p.backend.appointment.dto.AppointmentDto;
import pl.lodz.p.backend.appointment.dto.AppointmentFilterForm;
import pl.lodz.p.backend.appointment.dto.AppointmentStatusCountDto;
import pl.lodz.p.backend.appointment.dto.AvailableSlotDto;
import pl.lodz.p.backend.appointment.dto.CreateAppointmentForm;
import pl.lodz.p.backend.appointment.dto.HairOfferDto;
import pl.lodz.p.backend.appointment.dto.HairOfferStatisticDto;
import pl.lodz.p.backend.appointment.dto.HairdresserDto;
import pl.lodz.p.backend.appointment.dto.RescheduleAppointmentForm;
import pl.lodz.p.backend.common.controller.PageDto;
import pl.lodz.p.backend.common.controller.PageableRequest;
import pl.lodz.p.backend.common.controller.PageableUtils;
import pl.lodz.p.backend.common.exception.AlreadyExistException;
import pl.lodz.p.backend.common.exception.ErrorMessages;
import pl.lodz.p.backend.common.exception.NotFoundException;
import pl.lodz.p.backend.common.validation.DtoValidator;
import pl.lodz.p.backend.security.dto.UserDto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static pl.lodz.p.backend.common.exception.ErrorMessages.APPOINTMENT_NOT_FOUND;
import static pl.lodz.p.backend.common.repository.PageableUtils.convertToSpringPageable;

@Service
@RequiredArgsConstructor
class AppointmentService implements AppointmentFacade {
    private final AppointmentRepository appointmentRepository;
    private final HairOfferRepository hairOfferRepository;
    private final AppointmentUserRepository appointmentUserRepository;

    private static final LocalTime SALON_START_TIME = LocalTime.of(8, 0);
    private static final LocalTime SALON_END_TIME = LocalTime.of(17, 0);

    @Override
    public PageDto<AppointmentDto> findAllAppointments(final AppointmentFilterForm filterForm, final PageableRequest pageableRequest) {
        DtoValidator.validate(filterForm);
        DtoValidator.validate(pageableRequest);

        final AppointmentSpecification specification = new AppointmentSpecification(filterForm);
        final Page<AppointmentDto> accounts = appointmentRepository.findAll(specification, PageableUtils.createPageable(pageableRequest))
                .map(this::mapToDto);

        return PageableUtils.toDto(accounts);
    }

    @Override
    public PageDto<AppointmentDto> findAllAppointmentsSpecificUser(final UUID uuid, final AppointmentFilterForm filterForm, final PageableRequest pageableRequest) {
        Pageable pageable = convertToSpringPageable(pageableRequest);
        AppointmentStatus status = null;
        if (!Objects.isNull(filterForm.status())) {
            status = AppointmentStatus.valueOf(filterForm.status());
        }
        Page<AppointmentDto> appointments = appointmentRepository.findAllUserAppointments(uuid, status, filterForm.bookedDateStart(), filterForm.bookedDateEnd(), filterForm.hairdresserName(), filterForm.hairdresserSurname(), pageable).map(this::mapToDto);
        return PageableUtils.toDto(appointments);
    }

    @Override
    public List<AppointmentDto> findAllAppointmentsInSpecificDate(final LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);
        return appointmentRepository.findAllAppointmentsBetween(startOfDay, endOfDay).stream().map(this::mapToDto
        ).toList();
    }

    @Override
    public AppointmentDto findAppointmentByUuid(final UUID uuid) {
        return appointmentRepository.findByUuid(uuid)
                .map(this::mapToDto)
                .orElseThrow(() -> new NotFoundException(APPOINTMENT_NOT_FOUND));
    }

    private AppointmentDto mapToDto(final Appointment appointment) {
        return new AppointmentDto(appointment.getUuid(), appointment.getCustomer().getUuid(), appointment.getCustomer().getFirstname(), appointment.getCustomer().getSurname(), appointment.getHairdresser().getUuid(), appointment.getHairdresser().getFirstname(), appointment.getHairdresser().getSurname(), appointment.getTotalCost(), mapToHairOffersDto(appointment), appointment.getStatus().name(), appointment.getBookedDate(), appointment.getFinishedAppointmentDate());
    }

    private Set<HairOfferDto> mapToHairOffersDto(Appointment appointment) {
        return appointment.getHairOffers().stream()
                .map(hairOffer -> new HairOfferDto(hairOffer.getId(), hairOffer.getName(), hairOffer.getDescription(), hairOffer.getPrice(), hairOffer.getDuration())).collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public UUID makeAppointment(final CreateAppointmentForm createForm) {
        DtoValidator.validate(createForm);

        AppointmentUser customer = appointmentUserRepository.findByUuid(createForm.customerUuid())
                .orElseThrow(() -> new NotFoundException(ErrorMessages.USER_NOT_FOUND, createForm.customerUuid()));

        AppointmentUser hairdresser = appointmentUserRepository.findByUuid(createForm.hairdresserUuid())
                .orElseThrow(() -> new NotFoundException(ErrorMessages.USER_NOT_FOUND, createForm.hairdresserUuid()));

//        if (appointmentRepository.existsByBookedDateAndHairdresser(createForm.bookedDate(), hairdresser)) {
//            throw new AlreadyExistException(ErrorMessages.APPOINTMENT_EXIST);
//        }

        Set<AppointmentStatus> conflictingStatuses = Set.of(
                AppointmentStatus.CREATED,
                AppointmentStatus.ACCEPTED
        );
        if (appointmentRepository.existsByBookedDateAndHairdresserAndStatusIn(createForm.bookedDate(), hairdresser, conflictingStatuses)) {
            throw new AlreadyExistException(ErrorMessages.APPOINTMENT_EXIST);
        }

        Set<HairOffer> hairOffers = hairOfferRepository.findHairOffersByIdIn(createForm.hairoffers());
        BigDecimal totalCost = hairOffers.stream().map(HairOffer::getPrice).reduce(BigDecimal.valueOf(0), (a, b) -> a.add(b));

        Appointment appointment = new Appointment();
        appointment.setHairdresser(hairdresser);
        appointment.setCustomer(customer);
        hairOffers.forEach(appointment::addHairOffer);
        appointment.setBookedDate(createForm.bookedDate());
        appointment.setFinishedAppointmentDate(createForm.finishedDate());
        appointment.setTotalCost(totalCost);
        appointment.setStatus(AppointmentStatus.CREATED);

        return appointmentRepository.save(appointment).getUuid();
    }

    @Override
    @Transactional
    public void changeAppointmentStatus(final UUID uuid, final String status) {
        Appointment appointment = appointmentRepository.findByUuid(uuid).
                orElseThrow(() -> new NotFoundException(APPOINTMENT_NOT_FOUND));

        appointment.setStatus(AppointmentStatus.valueOf(status));
    }

    @Override
    @Transactional
    public void changeHairdresserAppointment(final UUID uuid, final UUID hairdresserUuid) {
        Appointment appointment = appointmentRepository.findByUuid(uuid).
                orElseThrow(() -> new NotFoundException(APPOINTMENT_NOT_FOUND));

        AppointmentUser hairdresser = appointmentUserRepository.findByUuid(hairdresserUuid)
                .orElseThrow(() -> new NotFoundException(ErrorMessages.USER_NOT_FOUND));

        appointment.setHairdresser(hairdresser);
    }

    @Override
    public boolean isHairdresserAvailable(final UUID hairdresserId, final LocalDateTime startDate, final Long totalDurations) {
        LocalDateTime requestedEnd = startDate.plusMinutes(totalDurations);

        List<Appointment> conflictingAppointments = appointmentRepository.findConflictingAppointments(
                hairdresserId, startDate, requestedEnd);

        return conflictingAppointments.isEmpty();
    }


    @Override
    public Set<HairdresserDto> getAllAvailableHairdressers(final LocalDateTime startDate, final Long totalDurations) {

        // 1. Pobierz wszystkich fryzjerów
        Set<HairdresserDto> hairdressers = appointmentUserRepository.findAllUsersByRole(RoleType.HAIRDRESSER)
                .stream().map(user -> new HairdresserDto(user.getUuid(), user.getFirstname(), user.getSurname()))
                .collect(Collectors.toSet());

        // 2. Dla każdego fryzjera sprawdź, czy jest wolny w wymaganym przedziale czasowym
        return hairdressers.stream()
                .filter(hairdresser -> isHairdresserAvailable(hairdresser.uuid(), startDate, totalDurations))
                .collect(Collectors.toSet());

    }

    @Override
    public PageDto<AppointmentDto> findAllClientAppointments(final Authentication authentication, final AppointmentFilterForm filterForm, final PageableRequest pageableRequest) {
        DtoValidator.validate(pageableRequest);
        UserDto customerAuth = (UserDto) authentication.getPrincipal();
        AppointmentUser customer = appointmentUserRepository.findUserByUsername(customerAuth.username())
                .orElseThrow(() -> new NotFoundException(ErrorMessages.USER_NOT_FOUND, customerAuth.username()));

        AppointmentStatus status = null;
        if (!Objects.isNull(filterForm.status())) {
            status = AppointmentStatus.valueOf(filterForm.status());
        }
        Pageable pageable = convertToSpringPageable(pageableRequest);
        final Page<AppointmentDto> appointments = appointmentRepository.findByCustomer_IdAndFilters(customer.getUuid(), status, filterForm.bookedDateStart(), filterForm.bookedDateEnd(), filterForm.hairdresserName(), filterForm.hairdresserSurname(), pageable)
                .map(this::mapToDto);

        return PageableUtils.toDto(appointments);
    }

    @Override
    public List<AvailableSlotDto> generateAvailableSlot(final LocalDate selectedDate, final Long totalHairOffersDurationMinutes, Authentication authentication) {
        List<AvailableSlotDto> availableSlots = new ArrayList<>();

        if (totalHairOffersDurationMinutes <= 0) {
            return availableSlots;
        }

        long totalSalonWorkMinutes = java.time.Duration.between(SALON_START_TIME, SALON_END_TIME).toMinutes();
        if (totalHairOffersDurationMinutes > totalSalonWorkMinutes) {
            return availableSlots; // Usługa jest dłuższa niż cały dzień pracy
        }

        Set<AppointmentUser> hairdressers = appointmentUserRepository.findAllUsersByRole(RoleType.HAIRDRESSER);
        if (hairdressers.isEmpty()) {
            return availableSlots;
        }

        UUID currentLoggedInUserUuid = null;
        String currentLoggedInUserRole = null;

        if (authentication != null && authentication.getPrincipal() instanceof UserDto) {
            UserDto currentUserDto = (UserDto) authentication.getPrincipal();
            currentLoggedInUserUuid = currentUserDto.userUuid();
            currentLoggedInUserRole = currentUserDto.role(); // Assuming userType holds the role string
        }

        final UUID finalCurrentLoggedInUserUuid = currentLoggedInUserUuid; // Make final for stream
        final String finalCurrentLoggedInUserRole = currentLoggedInUserRole; // Make final for stream

        if (finalCurrentLoggedInUserUuid != null && RoleType.HAIRDRESSER.name().equals(finalCurrentLoggedInUserRole)) {
            hairdressers = hairdressers.stream()
                    .filter(h -> !h.getUuid().equals(finalCurrentLoggedInUserUuid))
                    .collect(Collectors.toSet());
        }

        for (AppointmentUser hairdresser : hairdressers) {
            // ZMIANA TUTAJ: Zakres zapytania do bazy danych to teraz stricte godziny pracy salonu.
            // Oznacza to, że pobieramy tylko te spotkania, które zaczynają się w godzinach pracy salonu.
            LocalDateTime startOfWorkDay = selectedDate.atTime(SALON_START_TIME); // Od 9:00
            LocalDateTime endOfWorkDay = selectedDate.atTime(SALON_END_TIME);  // Do 17:00

            Set<AppointmentStatus> statusesToConsiderAsBooked = Set.of(
                    AppointmentStatus.CREATED,
                    AppointmentStatus.ACCEPTED
            );

            List<Appointment> existingAppointments = appointmentRepository.findByHairdresserAndBookedDateBetweenAndStatusInWithHairOffers(
                    hairdresser, startOfWorkDay, endOfWorkDay, statusesToConsiderAsBooked
            );

            // Musimy dodatkowo filtrować spotkania, które mogą *zaczynać się* w zakresie
            // ale *wykraczać* poza zakres, lub zaczynać się idealnie na końcu zakresu.
            // Baza danych zwróci tylko te, które mają bookedDate w przedziale 9:00-17:00.
            // Logika kolizji poniżej nadal poprawnie obsłuży te, które zachodzą na siebie.

            existingAppointments.sort(Comparator.comparing(Appointment::getBookedDate));

            LocalTime currentCheckTime = SALON_START_TIME; // Zawsze zaczynamy szukać wolnych slotów od 9:00

            while (currentCheckTime.plusMinutes(totalHairOffersDurationMinutes).isBefore(SALON_END_TIME) ||
                    currentCheckTime.plusMinutes(totalHairOffersDurationMinutes).equals(SALON_END_TIME)) {

                LocalTime potentialSlotStart = currentCheckTime;
                LocalTime potentialSlotEnd = potentialSlotStart.plusMinutes(totalHairOffersDurationMinutes);

                // Jeśli potencjalny slot kończy się po godzinach pracy salonu, to nie jest dostępny
                if (potentialSlotEnd.isAfter(SALON_END_TIME)) {
                    break;
                }

                boolean conflictFound = false;
                for (Appointment existingApp : existingAppointments) {
                    LocalTime existingAppStart = existingApp.getBookedDate().toLocalTime();
                    LocalTime existingAppEnd = existingAppStart.plusMinutes(getAppointmentDuration(existingApp));

                    // Kluczowa zmiana w obsłudze kolizji:
                    // Jeśli spotkanie zaczyna się przed SALON_START_TIME, ale kończy po SALON_START_TIME,
                    // to nadal blokuje czas od 9:00.
                    // Ale ponieważ zapytanie do BD jest już filtrowane do 9:00-17:00,
                    // musimy obsłużyć przypadek, gdy istniejące spotkanie zaczyna się np. o 16:30 i kończy o 17:30.
                    // W tej sytuacji, existingAppStart będzie 16:30, a existingAppEnd będzie 17:30.
                    // To jest ok, bo currentCheckTime będzie przesuwane za 17:30, co skutecznie zablokuje dalsze sloty.

                    // Warunek kolizji:
                    // Nowy slot (potentialSlotStart, potentialSlotEnd) koliduje z istniejącym (existingAppStart, existingAppEnd)
                    // jeśli: (początek nowego < koniec istniejącego) ORAZ (koniec nowego > początek istniejącego)
                    if (potentialSlotStart.isBefore(existingAppEnd) && potentialSlotEnd.isAfter(existingAppStart)) {
                        conflictFound = true;
                        // Jeśli jest konflikt, przesuń currentCheckTime do końca kolidującego spotkania.
                        // Ale niech nie przekracza SALON_END_TIME, jeśli to spotkanie wykracza poza.
                        currentCheckTime = existingAppEnd;
                        // Jeśli koniec istniejącego spotkania jest po SALON_END_TIME,
                        // to fryzjer i tak nie będzie dostępny do końca dnia, więc nie ma sensu szukać dalej.
                        if (currentCheckTime.isAfter(SALON_END_TIME)) {
                            currentCheckTime = SALON_END_TIME; // Efektywnie zablokuj resztę dnia
                            break; // Przerwij pętlę sprawdzającą konflikty dla tego fryzjera
                        }
                        break; // Przerwij pętlę sprawdzającą konflikty dla tego potentialSlotStart
                    }
                }

                if (!conflictFound) {
                    // Jeśli nie znaleziono konfliktu, dodaj ten slot do listy dostępnych
                    availableSlots.add(new AvailableSlotDto(
                            potentialSlotStart,
                            potentialSlotEnd,
                            hairdresser.getUuid(),
                            hairdresser.getFirstname(),
                            hairdresser.getSurname()
                    ));
                    // Po dodaniu slotu, przesuń currentCheckTime o całą długość znalezionego slotu.
                    currentCheckTime = potentialSlotEnd;
                }
                // Bezpiecznik: Jeśli z jakiegoś powodu currentCheckTime nie zostało przesunięte,
                // (np. luka jest za mała na totalHairOffersDurationMinutes, ale nie ma bezpośredniego konfliktu),
                // musimy przesunąć je, aby uniknąć nieskończonej pętli. Przesuwamy o 1 minutę.
                if (currentCheckTime.equals(potentialSlotStart)) {
                    currentCheckTime = currentCheckTime.plusMinutes(1);
                }
            }
        }

        availableSlots.sort(Comparator
                .comparing(AvailableSlotDto::startTime)
                .thenComparing(AvailableSlotDto::hairdresserName)
                .thenComparing(AvailableSlotDto::hairdresserSurname)
        );

        return availableSlots;
    }

    @Override
    public List<AppointmentStatusCountDto> getStatisticDataAppointmentsByStatus() {
        return appointmentRepository.countAppointmentsByStatusDto();
    }

    @Override
    public List<HairOfferStatisticDto> getStatisticCompletedHairOfferCounts() {
        return appointmentRepository.countCompletedHairOffers(AppointmentStatus.COMPLETED);
    }

    @Override
    @Transactional
    public void rescheduleAppointment(final UUID uuid, final RescheduleAppointmentForm form) {
        Appointment appointment = appointmentRepository.findByUuid(uuid).orElseThrow(() -> new NotFoundException(APPOINTMENT_NOT_FOUND, uuid));
        Long totalDuration = appointment.getHairOffers().stream().map(HairOffer::getDuration).reduce(0L, Long::sum);

        List<Appointment> conflicts = appointmentRepository.findConflictingAppointmentsExcludingCurrentNative(appointment.getHairdresser().getUuid(), form.newBookedDate(), form.newBookedDate().plusMinutes(totalDuration), uuid, Set.of(AppointmentStatus.CREATED.name(), AppointmentStatus.ACCEPTED.name()));
        if (!conflicts.isEmpty()) {
            throw new AlreadyExistException(ErrorMessages.APPOINTMENT_EXIST);
        }
        appointment.setBookedDate(form.newBookedDate());
        appointment.setFinishedAppointmentDate(form.newBookedDate().plusMinutes(totalDuration));
        appointment.setStatus(AppointmentStatus.RESCHEDULED);
        appointmentRepository.save(appointment);
    }

    @Override
    public PageDto<AppointmentDto> findAllAppointmentsWhereHairdresserIsCommon(final UUID uuid, final AppointmentFilterForm filterForm, final PageableRequest pageableRequest) {
        Pageable pageable = convertToSpringPageable(pageableRequest);
        AppointmentStatus status = null;
        if (!Objects.isNull(filterForm.status())) {
            status = AppointmentStatus.valueOf(filterForm.status());
        }
        Page<AppointmentDto> appointments = appointmentRepository.findAllHairdresserAppointments(uuid, status, filterForm.bookedDateStart(), filterForm.bookedDateEnd(), filterForm.hairdresserName(), filterForm.hairdresserSurname(), pageable).map(this::mapToDto);
        return PageableUtils.toDto(appointments);
    }


    private Long getAppointmentDuration(final Appointment appointment) {
        if (appointment.getHairOffers() == null || appointment.getHairOffers().isEmpty()) {
            return 0L; // Spotkanie bez usług, traktujemy jako 0 minut
        }
        return appointment.getHairOffers().stream().map(HairOffer::getDuration).reduce(0L, Long::sum);
    }
}
