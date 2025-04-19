package pl.lodz.p.backend.common.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import pl.lodz.p.backend.common.exception.AlreadyExistException;
import pl.lodz.p.backend.common.exception.NotFoundException;

import java.time.LocalDateTime;

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public CustomErrorResponse notFoundException(final NotFoundException exception) {
        return new CustomErrorResponse(HttpStatus.NOT_FOUND, exception.getMessage(), LocalDateTime.now());
    }

    @ExceptionHandler(AlreadyExistException.class)
    @ResponseStatus(value = HttpStatus.CONFLICT)
    public CustomErrorResponse alreadyExistException(final AlreadyExistException exception) {
        return new CustomErrorResponse(HttpStatus.CONFLICT, exception.getMessage(), LocalDateTime.now());
    }
}
