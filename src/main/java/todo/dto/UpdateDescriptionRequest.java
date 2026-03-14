package todo.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * Request DTO used to update the description of an existing todo item.
 *
 * This object is sent in the request body of:
 * PATCH /todos/{id}/description
 *
 * Validation rules:
 * - description must not be blank
 */
@Getter
@Setter
public class UpdateDescriptionRequest {

    /**
     * New description of the todo item.
     *
     * Must not be blank.
     */
    @NotBlank
    private String description;
}