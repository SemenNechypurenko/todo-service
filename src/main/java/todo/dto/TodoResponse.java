package todo.dto;

import lombok.*;

import java.time.Instant;

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
     */
    private String status;

    /**
     * Timestamp when the todo was created.
     */
    private Instant createdAt;

    /**
     * Optional due date of the todo item.
     */
    private Instant dueDate;

    /**
     * Timestamp when the todo was marked as done.
     */
    private Instant doneAt;
}