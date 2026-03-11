package todo.model;

/**
 * Enumeration representing the possible statuses of a Todo item.
 * <p>
 * - NOT_DONE: The todo has not been completed yet.
 * - DONE: The todo has been completed.
 * - PAST_DUE: The todo was not completed before its due date and is now past due.
 */
public enum TodoStatus {

    /** The todo item has not been completed yet. */
    NOT_DONE,

    /** The todo item has been completed. */
    DONE,

    /** The todo item is past its due date and cannot be modified. */
    PAST_DUE
}
