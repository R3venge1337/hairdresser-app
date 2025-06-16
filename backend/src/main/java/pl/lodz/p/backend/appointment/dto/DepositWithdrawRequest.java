package pl.lodz.p.backend.appointment.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record DepositWithdrawRequest(
        @NotNull(message = "Amount cannot be null")
        @DecimalMin(value = "0.01", message = "Amount must be positive")
        BigDecimal amount
) {}
