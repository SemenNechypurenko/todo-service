package todo.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.Instant;

/**
 * Request DTO used to create a new Todo item.
 *
 * Contains the minimal data required to create a todo:
 * - description (required)
 * - optional due date
 *
 * Validation rules:
 * - description must not be blank
 * - dueDate must be in the future if provided
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TodoRequest {

    /**
     * Description of the todo item.
     *
     * Must not be blank.
     */
    @NotBlank
    private String description;

    /**
     * Optional due date of the todo.
     *
     * If provided, it must be a future timestamp.
     */
    @Future
    private Instant dueDate;
}