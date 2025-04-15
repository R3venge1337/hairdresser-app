package pl.lodz.p.backend.common.domain;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import java.util.UUID;

@MappedSuperclass
@Getter
@FieldNameConstants
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public abstract class AbstractUuidEntity extends AbstractIdEntity {

    @EqualsAndHashCode.Include
    @Setter(AccessLevel.NONE)
    @Column(name = "uuid", unique = true, nullable = false)
    protected UUID uuid = UUID.randomUUID();

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + '(' + "uuid=" + uuid + ')';
    }
}
