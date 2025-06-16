package pl.lodz.p.backend.appointment.dto;

import java.math.BigDecimal;

public record HairOfferDto(Long id, String name, String description, BigDecimal price, Long duration) {
}
