package todo.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

/**
 * Data Transfer Object (DTO) representing a structured error response
 * returned by the GlobalExceptionHandler.
 * <p>
 * Provides a human-readable message, HTTP status code, and timestamp
 * of when the error occurred.
 */
@Getter
@Builder
public class ErrorResponse {

    /**
     * A descriptive error message explaining what went wrong.
     */
    private String message;

    /**
     * The HTTP status code corresponding to the error (e.g., 404, 400, 500).
     */
    private int status;

    /**
     * The timestamp indicating when the error occurred.
     */
    private Instant timestamp;
}