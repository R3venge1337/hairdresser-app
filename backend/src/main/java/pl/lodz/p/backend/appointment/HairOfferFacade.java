package pl.lodz.p.backend.appointment;

import pl.lodz.p.backend.appointment.dto.CreateHairOfferForm;
import pl.lodz.p.backend.appointment.dto.HairOfferDto;
import pl.lodz.p.backend.appointment.dto.HairOfferFilterForm;
import pl.lodz.p.backend.appointment.dto.UpdateHairOfferForm;
import pl.lodz.p.backend.common.controller.PageDto;
import pl.lodz.p.backend.common.controller.PageableRequest;

public interface HairOfferFacade {
    PageDto<HairOfferDto> findAllHairOffers(final HairOfferFilterForm hairOfferFilterForm, final PageableRequest pageableRequest);

    HairOfferDto findHairOfferById(final Long id);

    void createHairOffer(final CreateHairOfferForm createForm);

    void updateHairOffer(final Long id, final UpdateHairOfferForm updateForm);

    void deleteHairOffer(final Long id);
}
