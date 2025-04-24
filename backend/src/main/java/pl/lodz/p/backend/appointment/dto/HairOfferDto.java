package pl.lodz.p.backend.appointment.dto;

import java.math.BigDecimal;

public record HairOfferDto(String name, String description, BigDecimal price, Long duration) {
}
