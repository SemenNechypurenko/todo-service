package todo.exception;

/**
 * Exception thrown when a Todo item with the specified ID
 * cannot be found in the database.
 * <p>
 * This exception is handled by {@link GlobalExceptionHandler}
 * and returns HTTP 404 NOT_FOUND.
 */
public class TodoNotFoundException extends RuntimeException {

    /**
     * Constructs a new TodoNotFoundException for the given todo ID.
     *
     * @param id the ID of the todo that was not found
     */
    public TodoNotFoundException(Long id) {
        super("Todo with id " + id + " not found");
    }
}
