package todo.exception;

/**
 * Exception thrown when attempting to modify a Todo item
 * that has a status of PAST_DUE.
 * <p>
 * This exception is handled by {@link GlobalExceptionHandler}
 * and returns HTTP 409 CONFLICT.
 */
public class PastDueModificationException extends RuntimeException {

    /**
     * Constructs a new PastDueModificationException for the given todo ID.
     *
     * @param id the ID of the todo that cannot be modified
     */
    public PastDueModificationException(Long id) {
        super("Todo " + id + " is PAST_DUE and cannot be modified");
    }
}
