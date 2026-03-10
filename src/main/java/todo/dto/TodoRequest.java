package todo.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TodoRequest {

    private String description;

    // Optional
    private LocalDateTime dueDate;

}
