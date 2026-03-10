package todo.exception;

public class PastDueModificationException extends RuntimeException {

    public PastDueModificationException(Long id) {
        super("Todo " + id + " is PAST_DUE and cannot be modified");
    }
}