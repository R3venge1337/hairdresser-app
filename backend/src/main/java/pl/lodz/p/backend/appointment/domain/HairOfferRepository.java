package pl.lodz.p.backend.appointment.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Set;

interface HairOfferRepository extends JpaRepository<HairOffer, Long>, JpaSpecificationExecutor<HairOffer> {
    Boolean existsByName(final String name);
    Set<HairOffer> findHairOffersByIdIn(List<Long> ids);
}
