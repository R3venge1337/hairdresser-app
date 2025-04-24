package pl.lodz.p.backend.appointment.domain;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import pl.lodz.p.backend.appointment.dto.AppointmentFilterForm;

import java.util.ArrayList;
import java.util.List;

import static pl.lodz.p.backend.common.repository.PredicateUtils.addEqualPredicate;
import static pl.lodz.p.backend.common.repository.PredicateUtils.addLikePredicate;
import static pl.lodz.p.backend.common.repository.PredicateUtils.addLocalDateTimeBetweenPredicate;
import static pl.lodz.p.backend.common.repository.PredicateUtils.buildAndPredicates;

@RequiredArgsConstructor
class AppointmentSpecification implements Specification<Appointment> {
    private final AppointmentFilterForm filterForm;

    @Override
    public Predicate toPredicate(Root<Appointment> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        final List<Predicate> predicates = new ArrayList<>();

        addLikePredicate(criteriaBuilder, predicates, root.get(Appointment.Fields.hairdresser), filterForm.hairdresserName() + filterForm.hairdresserSurname());
        addLocalDateTimeBetweenPredicate(criteriaBuilder, predicates, root.get(Appointment.Fields.bookedDate), filterForm.bookedDateStart(), filterForm.bookedDateEnd());
        addEqualPredicate(criteriaBuilder, predicates, root.get(Appointment.Fields.status), filterForm.status());

        return buildAndPredicates(criteriaBuilder, predicates);
    }
}
