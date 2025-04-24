package pl.lodz.p.backend.appointment.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;
import pl.lodz.p.backend.common.domain.AbstractIdEntity;

@Entity
@Table(name = "roles")
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@FieldNameConstants
@NoArgsConstructor
class AppointmentRole extends AbstractIdEntity {

    @Enumerated(EnumType.STRING)
    private RoleType name;

}
