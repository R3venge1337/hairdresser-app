package pl.lodz.p.backend.appointment.domain;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import pl.lodz.p.backend.appointment.AppointmentFacade;
import pl.lodz.p.backend.appointment.dto.AppointmentDto;
import pl.lodz.p.backend.appointment.dto.AppointmentFilterForm;
import pl.lodz.p.backend.appointment.dto.CreateAppointmentForm;
import pl.lodz.p.backend.appointment.dto.HairOfferDto;
import pl.lodz.p.backend.common.controller.PageDto;
import pl.lodz.p.backend.common.controller.PageableRequest;
import pl.lodz.p.backend.common.controller.PageableUtils;
import pl.lodz.p.backend.common.exception.AlreadyExistException;
import pl.lodz.p.backend.common.exception.ErrorMessages;
import pl.lodz.p.backend.common.exception.NotFoundException;
import pl.lodz.p.backend.common.validation.DtoValidator;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static pl.lodz.p.backend.common.exception.ErrorMessages.APPOINTMENT_NOT_FOUND;

@Service
@RequiredArgsConstructor
class AppointmentService implements AppointmentFacade {
    private final AppointmentRepository appointmentRepository;
    private final HairOfferRepository hairOfferRepository;
    private final AppointmentUserRepository appointmentUserRepository;

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
    public AppointmentDto findAppointmentByUuid(final UUID uuid) {
        return appointmentRepository.findByUuid(uuid)
                .map(this::mapToDto)
                .orElseThrow(() -> new NotFoundException(APPOINTMENT_NOT_FOUND));
    }

    private AppointmentDto mapToDto(final Appointment appointment) {
        return new AppointmentDto(appointment.getUuid(), appointment.getCustomer().getFirstname(), appointment.getCustomer().getSurname(), appointment.getHairdresser().getUuid(), appointment.getHairdresser().getFirstname(), appointment.getHairdresser().getSurname(), appointment.getTotalCost(), mapToHairOffersDto(appointment), appointment.getStatus().name(), appointment.getBookedDate());
    }

    private Set<HairOfferDto> mapToHairOffersDto(Appointment appointment) {
        return appointment.getHairOffers().stream()
                .map(hairOffer -> new HairOfferDto(hairOffer.getName(), hairOffer.getDescription(), hairOffer.getPrice(), hairOffer.getDuration())).collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public UUID makeAppointment(final CreateAppointmentForm createForm) {
        DtoValidator.validate(createForm);

        AppointmentUser customer = appointmentUserRepository.findByUuid(createForm.customerUuid())
                .orElseThrow(() -> new NotFoundException(ErrorMessages.USER_NOT_FOUND, createForm.customerUuid()));

        AppointmentUser hairdresser = appointmentUserRepository.findByUuid(createForm.hairDresserUuid())
                .orElseThrow(() -> new NotFoundException(ErrorMessages.USER_NOT_FOUND, createForm.hairDresserUuid()));

        if (appointmentRepository.existsByBookedDateAndHairdresser(createForm.bookedDate(), hairdresser)) {
            throw new AlreadyExistException(ErrorMessages.APPOINTMENT_EXIST);
        }

        Set<HairOffer> hairOffers = hairOfferRepository.findHairOffersByIdIn(createForm.hairOffers());

        Appointment appointment = new Appointment();
        appointment.setHairdresser(hairdresser);
        appointment.setCustomer(customer);
        hairOffers.forEach(appointment::addHairOffer);
        appointment.setBookedDate(createForm.bookedDate());
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
}
