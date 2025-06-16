package pl.lodz.p.backend.common.exception;

import java.util.UUID;

public class InsufficientFundsException extends RuntimeException {
  public InsufficientFundsException(final String message) {
    super(message);
  }

  public InsufficientFundsException(final UUID uuid) {
    super(String.format("Entity already exist by uuid: %s", uuid));
  }

  public InsufficientFundsException(final String message, final Object... args) {
    super(String.format(message, args));
  }

}
