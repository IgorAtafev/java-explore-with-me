package ru.yandex.practicum.ewm.validator;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolationException;
import java.io.IOException;

@ControllerAdvice
@Slf4j
public class ErrorHandler {

    @ExceptionHandler({
            MethodArgumentNotValidException.class,
            ConstraintViolationException.class
    })
    public void handleValidationException(final Exception e, final HttpServletResponse response)
            throws IOException {
        log.error(e.getMessage(), e);
        sendError(response, HttpStatus.BAD_REQUEST.value(), e.getMessage());
    }

    @ExceptionHandler
    public void handleNotFoundException(final NotFoundException e, final HttpServletResponse response)
            throws IOException {
        log.error(e.getMessage(), e);
        sendError(response, HttpStatus.NOT_FOUND.value(), e.getMessage());
    }

    @ExceptionHandler
    public void handleConflictException(final ConflictException e, final HttpServletResponse response)
            throws IOException {
        log.info(e.getMessage(), e);
        sendError(response, HttpStatus.CONFLICT.value(), e.getMessage());
    }

    @ExceptionHandler
    public void handleThrowable(final Throwable e, final HttpServletResponse response) throws IOException {
        log.error(e.getMessage(), e);
        sendError(response, HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage());
    }

    private void sendError(final HttpServletResponse response, int httpStatusCode, String errorMessage)
            throws IOException {
        response.setStatus(httpStatusCode);
        response.setHeader("Content-Type", "application/json");
        response.getOutputStream().print("{\"error\": \"" + errorMessage + "\"}");
    }
}
