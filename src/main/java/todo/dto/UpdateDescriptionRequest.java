package todo.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO used for updating the description of a todo item.
 */
@Getter
@Setter
public class UpdateDescriptionRequest {

    @NotBlank
    private String description;
}