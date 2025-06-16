package pl.lodz.p.backend.appointment.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.lodz.p.backend.appointment.HairdresserFacade;
import pl.lodz.p.backend.common.exception.AlreadyExistException;
import pl.lodz.p.backend.common.exception.ErrorMessages;
import pl.lodz.p.backend.common.validation.DtoValidator;
import pl.lodz.p.backend.security.PasswordFacade;
import pl.lodz.p.backend.security.dto.RegisterForm;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class HairdresserService implements HairdresserFacade {

    private final AppointmentUserRepository appointmentUserRepository;
    private final AppointmentAccountRepository appointmentAccountRepository;
    private final AppointmentRoleRepository appointmentRoleRepository;
    private final PasswordFacade passwordFacade;

    private static AppointmentUser createAppUser(final RegisterForm registerForm, final AppointmentAccount accountEntity) {
        AppointmentUser user = new AppointmentUser();
        user.setFirstname(registerForm.firstname());
        user.setSurname(registerForm.surname());
        user.setPhoneNumber(registerForm.phoneNumber());
        user.setAppointmentAccount(accountEntity);
        return user;
    }

    private AppointmentAccount createAccount(final RegisterForm registerForm, final AppointmentRole role) {
        AppointmentAccount account = new AppointmentAccount();
        account.setEmail(registerForm.email());
        account.setUsername(registerForm.username());
        account.setPassword(passwordFacade.encodePassword(registerForm.password()));
        account.setIsActive(true);
        account.setCreatedDate(LocalDateTime.now());
        account.setAppointmentRole(role);
        return account;
    }

    @Override
    public void addHairdresser(final RegisterForm registerForm) {
        DtoValidator.validate(registerForm);
        if (appointmentAccountRepository.existsByEmail(registerForm.email())) {
            throw new AlreadyExistException(ErrorMessages.USER_EXIST_BY_EMAIL);
        }
        if (appointmentAccountRepository.existsByUsername(registerForm.username())) {
            throw new AlreadyExistException(ErrorMessages.USER_EXIST_BY_NAME);
        }
        final AppointmentRole role = appointmentRoleRepository.findByName(RoleType.HAIRDRESSER).orElse(null);
        final AppointmentAccount account = createAccount(registerForm, role);
        final AppointmentAccount accountEntity = appointmentAccountRepository.save(account);
        final AppointmentUser user = createAppUser(registerForm, accountEntity);

        appointmentUserRepository.save(user);
    }


}
