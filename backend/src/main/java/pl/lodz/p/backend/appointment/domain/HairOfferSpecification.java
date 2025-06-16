package pl.lodz.p.backend.appointment.domain;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import pl.lodz.p.backend.appointment.dto.HairOfferFilterForm;

import java.util.ArrayList;
import java.util.List;

import static pl.lodz.p.backend.common.repository.PredicateUtils.addBetweenPredicate;
import static pl.lodz.p.backend.common.repository.PredicateUtils.addBigDecimalBetweenPredicate;
import static pl.lodz.p.backend.common.repository.PredicateUtils.addLikePredicate;
import static pl.lodz.p.backend.common.repository.PredicateUtils.buildAndPredicates;

@RequiredArgsConstructor
class HairOfferSpecification implements Specification<HairOffer> {

    private final HairOfferFilterForm hairOfferFilterForm;

    @Override
    public Predicate toPredicate(Root<HairOffer> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        final List<Predicate> predicates = new ArrayList<>();


        addLikePredicate(criteriaBuilder, predicates, root.get(HairOffer.Fields.name), hairOfferFilterForm.name());
        addBigDecimalBetweenPredicate(criteriaBuilder, predicates, root.get(HairOffer.Fields.price), hairOfferFilterForm.priceLow(), hairOfferFilterForm.priceHigh());
        addBetweenPredicate(criteriaBuilder, predicates, root.get(HairOffer.Fields.duration), hairOfferFilterForm.durationLow(), hairOfferFilterForm.durationHigh(), Long.class);

        return buildAndPredicates(criteriaBuilder, predicates);
    }
}
