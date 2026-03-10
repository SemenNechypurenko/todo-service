package todo.exception;

public class InvalidDueDateException extends RuntimeException {

    public InvalidDueDateException() {
        super("Due date must be in the future");
    }
}