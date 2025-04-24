package pl.lodz.p.backend.appointment.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import pl.lodz.p.backend.appointment.HairOfferFacade;
import pl.lodz.p.backend.appointment.dto.CreateHairOfferForm;
import pl.lodz.p.backend.appointment.dto.HairOfferDto;
import pl.lodz.p.backend.appointment.dto.HairOfferFilterForm;
import pl.lodz.p.backend.appointment.dto.UpdateHairOfferForm;
import pl.lodz.p.backend.common.controller.PageDto;
import pl.lodz.p.backend.common.controller.PageableRequest;
import pl.lodz.p.backend.common.controller.PageableUtils;
import pl.lodz.p.backend.common.exception.AlreadyExistException;
import pl.lodz.p.backend.common.exception.ErrorMessages;
import pl.lodz.p.backend.common.exception.NotFoundException;
import pl.lodz.p.backend.common.validation.DtoValidator;

import static pl.lodz.p.backend.common.exception.ErrorMessages.HAIR_OFFER_NOT_FOUND;

@Service
@RequiredArgsConstructor
class HairOfferService implements HairOfferFacade {
    private final HairOfferRepository hairOfferRepository;

    @Override
    public PageDto<HairOfferDto> findAllHairOffers(final HairOfferFilterForm filterForm, final PageableRequest pageableRequest) {
        DtoValidator.validate(filterForm);
        DtoValidator.validate(pageableRequest);

        final HairOfferSpecification specification = new HairOfferSpecification(filterForm);
        final Page<HairOfferDto> hairOffers = hairOfferRepository.findAll(specification, PageableUtils.createPageable(pageableRequest))
                .map(this::mapToDto);

        return PageableUtils.toDto(hairOffers);
    }

    @Override
    public HairOfferDto findHairOfferById(final Long id) {
        return hairOfferRepository.findById(id)
                .map(this::mapToDto)
                .orElseThrow(() -> new NotFoundException(HAIR_OFFER_NOT_FOUND));
    }

    private HairOfferDto mapToDto(final HairOffer hairOffer) {
        return new HairOfferDto(hairOffer.getName(), hairOffer.getDescription(), hairOffer.getPrice(), hairOffer.getDuration());
    }

    @Override
    public void createHairOffer(final CreateHairOfferForm createForm) {
        DtoValidator.validate(createForm);

        checkUnique(createForm.name());

        final HairOffer hairOffer = new HairOffer();
        hairOffer.setName(createForm.name());
        hairOffer.setDescription(createForm.description());
        hairOffer.setPrice(createForm.price());
        hairOffer.setDuration(createForm.duration());

        hairOfferRepository.save(hairOffer);

    }

    @Override
    public void updateHairOffer(final Long id, final UpdateHairOfferForm updateForm) {
        DtoValidator.validate(updateForm);

        final HairOffer hairOffer = hairOfferRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(HAIR_OFFER_NOT_FOUND));

        checkUnique(updateForm.name(), hairOffer.getName());

        hairOffer.setName(updateForm.name());
        hairOffer.setDescription(updateForm.description());
        hairOffer.setPrice(updateForm.price());
        hairOffer.setDuration(updateForm.duration());

    }

    @Override
    public void deleteHairOffer(final Long id) {
        hairOfferRepository.deleteById(id);
    }

    private void checkUnique(final String formLogin, final String entityLogin) {
        if (!formLogin.equals(entityLogin)) {
            checkUnique(formLogin);
        }
    }

    private void checkUnique(final String offerName) {
        if (hairOfferRepository.existsByName(offerName)) {
            throw new AlreadyExistException(ErrorMessages.HAIR_OFFER_EXIST_BY_NAME);
        }
    }
}
