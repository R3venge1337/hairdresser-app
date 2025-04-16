package pl.lodz.p.backend.common.validation;

public record ValidationErrorDto(String field, String message) {
    public ValidationErrorDto(String field, ValidationMessage message) {
        this(field, message.name());
    }

    public ValidationErrorDto(final String message) {
        this(null, message);
    }
}