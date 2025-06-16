package pl.lodz.p.backend.appointment.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import pl.lodz.p.backend.appointment.HairdresserFacade;
import pl.lodz.p.backend.common.controller.RoutePaths;
import pl.lodz.p.backend.security.dto.RegisterForm;

@RestController
@RequiredArgsConstructor
class HairdresserController {

    private final HairdresserFacade hairdresserFacade;

    @PostMapping(RoutePaths.HAIRDRESSERS)
    @ResponseStatus(HttpStatus.CREATED)
    void addHairdresser(@Valid @RequestBody final RegisterForm registerForm) {
        hairdresserFacade.addHairdresser(registerForm);
    }

}
