package todo.dto;

import lombok.*;

import java.time.LocalDateTime;

/**
 * Data Transfer Object (DTO) representing a request to create or update a Todo item.
 * <p>
 * Contains the description and optional due date for a todo.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TodoRequest {

    /**
     * The description of the todo item.
     * <p>
     * This field is required when creating a new todo.
     */
    private String description;

    /**
     * The optional due date for the todo item.
     * <p>
     * If provided, it must be in the future; otherwise, an InvalidDueDateException is thrown.
     */
    private LocalDateTime dueDate;
}
