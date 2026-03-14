package todo.exception;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import todo.dto.ErrorResponse;

import java.time.LocalDateTime;

/**
 * Global exception handler for the Todo Service.
 * <p>
 * Catches both specific and unexpected exceptions, returning
 * appropriate HTTP status codes and structured error responses.
 * Logs warnings and errors for debugging purposes.
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * Handles cases where a todo with the specified ID is not found.
     * Returns HTTP 404 NOT_FOUND.
     *
     * @param ex the exception thrown when a todo is not found
     * @return a structured error response
     */
    @ExceptionHandler(TodoNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFound(TodoNotFoundException ex) {

        log.warn("Todo not found: {}", ex.getMessage());

        return ErrorResponse.builder()
                .message(ex.getMessage())
                .status(HttpStatus.NOT_FOUND.value())
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * Handles attempts to modify todos that are past due (PAST_DUE status).
     * Returns HTTP 409 CONFLICT.
     *
     * @param ex the exception thrown when trying to modify a past due todo
     * @return a structured error response
     */
    @ExceptionHandler(PastDueModificationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handlePastDue(PastDueModificationException ex) {

        log.warn("Past due modification attempt: {}", ex.getMessage());

        return ErrorResponse.builder()
                .message(ex.getMessage())
                .status(HttpStatus.CONFLICT.value())
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * Handles invalid due date exceptions, such as due dates in the past
     * or invalid date formats. Returns HTTP 400 BAD_REQUEST.
     *
     * @param ex the exception thrown when a due date is invalid
     * @return a structured error response
     */
    @ExceptionHandler(InvalidDueDateException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleInvalidDueDate(InvalidDueDateException ex) {

        log.warn("Invalid due date: {}", ex.getMessage());

        return ErrorResponse.builder()
                .message(ex.getMessage())
                .status(HttpStatus.BAD_REQUEST.value())
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * Generic handler for all unexpected exceptions.
     * Returns HTTP 500 INTERNAL_SERVER_ERROR.
     *
     * @param ex the unexpected exception
     * @return a structured error response
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleGeneric(Exception ex) {

        log.error("Unexpected error", ex);

        return ErrorResponse.builder()
                .message("Internal server error")
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * Handles validation errors for @Valid request bodies.
     * Returns HTTP 400 BAD_REQUEST.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidation(MethodArgumentNotValidException ex) {

        FieldError error = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .findFirst()
                .orElse(null);

        String message = error != null
                ? error.getField() + " " + error.getDefaultMessage()
                : "Validation error";

        log.warn("Validation error: {}", message);

        return ErrorResponse.builder()
                .message(message)
                .status(HttpStatus.BAD_REQUEST.value())
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * Handles validation errors for request parameters and path variables.
     * Returns HTTP 400 BAD_REQUEST.
     */
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleConstraintViolation(ConstraintViolationException ex) {

        String message = ex.getConstraintViolations()
                .stream()
                .findFirst()
                .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                .orElse("Validation error");

        log.warn("Constraint violation: {}", message);

        return ErrorResponse.builder()
                .message(message)
                .status(HttpStatus.BAD_REQUEST.value())
                .timestamp(LocalDateTime.now())
                .build();
    }
}