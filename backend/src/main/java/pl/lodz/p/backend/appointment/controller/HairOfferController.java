package pl.lodz.p.backend.appointment.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import pl.lodz.p.backend.appointment.HairOfferFacade;
import pl.lodz.p.backend.appointment.dto.CreateHairOfferForm;
import pl.lodz.p.backend.appointment.dto.HairOfferDto;
import pl.lodz.p.backend.appointment.dto.HairOfferFilterForm;
import pl.lodz.p.backend.appointment.dto.UpdateHairOfferForm;
import pl.lodz.p.backend.common.controller.PageDto;
import pl.lodz.p.backend.common.controller.PageableRequest;
import pl.lodz.p.backend.common.controller.RoutePaths;

@RestController
@RequiredArgsConstructor
class HairOfferController {
    private final HairOfferFacade hairOfferFacade;

    @GetMapping(RoutePaths.HAIR_OFFERS)
    PageDto<HairOfferDto> findAllHairOffers(@RequestBody final HairOfferFilterForm filterForm, final PageableRequest pageableRequest){
        return hairOfferFacade.findAllHairOffers(filterForm, pageableRequest);
    }

    @GetMapping(RoutePaths.HAIR_OFFERS + "/{id}")
    HairOfferDto findHairOfferById(@PathVariable final Long id){
        return hairOfferFacade.findHairOfferById(id);
    }

    @PostMapping(RoutePaths.HAIR_OFFERS)
    @ResponseStatus(HttpStatus.CREATED)
    void createHairOffer(@RequestBody final CreateHairOfferForm createForm){
         hairOfferFacade.createHairOffer(createForm);
    }

    @PutMapping(RoutePaths.HAIR_OFFERS  + "/{id}")
    void updateHairOffer(@PathVariable final Long id, @RequestBody final UpdateHairOfferForm updateForm){
         hairOfferFacade.updateHairOffer(id, updateForm);
    }
}
