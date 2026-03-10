package todo.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TodoResponse {

    private Long id;
    private String description;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime dueDate;
    private LocalDateTime doneAt;

}
