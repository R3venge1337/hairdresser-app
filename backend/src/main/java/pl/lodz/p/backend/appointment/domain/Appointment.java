package pl.lodz.p.backend.appointment.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;
import pl.lodz.p.backend.common.domain.AbstractUuidEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "appointments")
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@FieldNameConstants
class Appointment extends AbstractUuidEntity {

    @OneToOne
    @JoinColumn(name = "customer_id_fk")
    private AppointmentUser customer;

    @OneToOne
    @JoinColumn(name = "hairdresser_id_fk")
    private AppointmentUser hairdresser;

    @OneToMany(
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @JoinTable(
            name = "appointments_hairoffers",
            joinColumns = @JoinColumn(name = "appointment_id_fk"),
            inverseJoinColumns = @JoinColumn(name = "hairoffer_id_fk")
    )
    private Set<HairOffer> hairOffers = new HashSet<>();

    @Column(name = "totalCost")
    private BigDecimal totalCost;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private AppointmentStatus status;

    @Column(name = "bookedDate")
    private LocalDateTime bookedDate;

    public void addHairOffer(HairOffer hairOffer) {
        hairOffers.add(hairOffer);
    }

    public void removeHairOffer(HairOffer hairOffer) {
        hairOffers.remove(hairOffer);
    }
}
