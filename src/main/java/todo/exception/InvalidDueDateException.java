package todo.exception;

/**
 * Exception thrown when a provided due date for a Todo item
 * is invalid, such as being in the past.
 * <p>
 * This exception is handled by {@link GlobalExceptionHandler}
 * and returns HTTP 400 BAD_REQUEST.
 */
public class InvalidDueDateException extends RuntimeException {

    /**
     * Constructs a new InvalidDueDateException with a default message.
     */
    public InvalidDueDateException() {
        super("Due date must be in the future");
    }
}
