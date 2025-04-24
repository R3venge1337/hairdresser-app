package pl.lodz.p.backend.appointment.domain;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;
import pl.lodz.p.backend.common.domain.AbstractUuidEntity;

@Entity
@Table(name = "users")
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@FieldNameConstants
class AppointmentUser extends AbstractUuidEntity {

    @Column(name = "firstname")
    private String firstname;

    @Column(name = "surname")
    private String surname;

    @Column(name = "phone")
    private String phoneNumber;

    @OneToOne
    @JoinColumn(name = "account_id_fk")
    private AppointmentAccount appointmentAccount;

}
