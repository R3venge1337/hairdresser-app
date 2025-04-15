package pl.lodz.p.backend.security.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;
import pl.lodz.p.backend.common.domain.AbstractIdEntity;

import java.time.LocalDateTime;

@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@Entity
@Table(name = "accounts")
@FieldNameConstants
class Account extends AbstractIdEntity {

        @Column(name = "nickname")
        private String nickname;

        @Column(name = "password")
        private String password;

        @Column(name = "date_created")
        private LocalDateTime createdDate;

        @Column(name = "email")
        private String email;

        @Column(name = "is_active")
        private Boolean isActive;

        @ManyToOne(fetch = FetchType.EAGER)
        @JoinColumn(name = "role_id_fk")
        private Role role;
}
