package todo.dto;

import lombok.*;

import java.time.LocalDateTime;

/**
 * Data Transfer Object (DTO) representing a response for a Todo item.
 * <p>
 * Used for returning todo details in API responses.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TodoResponse {

    /**
     * Unique identifier of the todo item.
     */
    private Long id;

    /**
     * Description of the todo item.
     */
    private String description;

    /**
     * Current status of the todo item.
     * <p>
     * Possible values: "NOT_DONE", "DONE", "PAST_DUE".
     */
    private String status;

    /**
     * Timestamp when the todo was created.
     */
    private LocalDateTime createdAt;

    /**
     * Optional due date of the todo item.
     * <p>
     * Can be null if not set.
     */
    private LocalDateTime dueDate;

    /**
     * Timestamp when the todo was marked as done.
     * <p>
     * Null if the todo is not completed yet.
     */
    private LocalDateTime doneAt;
}