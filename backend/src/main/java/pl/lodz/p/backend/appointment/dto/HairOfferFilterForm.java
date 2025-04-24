package pl.lodz.p.backend.appointment.dto;

import java.math.BigDecimal;

public record HairOfferFilterForm(String name, BigDecimal priceLow, BigDecimal priceHigh, Long durationLow, Long durationHigh) {
}
