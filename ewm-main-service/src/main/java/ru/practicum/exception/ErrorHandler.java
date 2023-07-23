package ru.practicum.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolationException;
import java.time.LocalDateTime;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {
    private static final String ISE_REASON = "Something went wrong.";

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorApi handleException(Exception exception) {
        log.error("Error", exception);
        return new ErrorApi(HttpStatus.INTERNAL_SERVER_ERROR, ISE_REASON, exception.getMessage(), LocalDateTime.now());
    }

    @ExceptionHandler({DataIntegrityViolationException.class, ConflictDataException.class})
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorApi handleConstraintViolationException(RuntimeException exception) {
        log.info("ConstraintViolationException", exception);
        return new ErrorApi(
                HttpStatus.CONFLICT,
                "Integrity constraint has been violated.",
                exception.getLocalizedMessage(),
                LocalDateTime.now()
        );
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, ValidationException.class, ConstraintViolationException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorApi handleValidationException(RuntimeException exception) {
        log.info(exception.toString());
        return new ErrorApi(
                HttpStatus.BAD_REQUEST,
                "Incorrectly made request.",
                exception.getLocalizedMessage(),
                LocalDateTime.now());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorApi handleValidationException(NotFoundException exception) {
        log.info(exception.toString());
        return new ErrorApi(
                HttpStatus.NOT_FOUND,
                "Object not found.",
                exception.getLocalizedMessage(),
                LocalDateTime.now());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorApi handleInvalidDataException(InvalidDataException exception) {
        log.info(exception.toString());
        return new ErrorApi(
                HttpStatus.FORBIDDEN,
                "Invalid event data.",
                exception.getLocalizedMessage(),
                LocalDateTime.now());
    }
}
