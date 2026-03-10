package todo.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import todo.dto.TodoRequest;
import todo.dto.TodoResponse;
import todo.model.TodoItem;
import todo.model.TodoStatus;
import todo.repository.TodoRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TodoService {

    private final TodoRepository todoRepository;

    /**
     * Creates a new todo.
     */
    @Transactional
    public TodoResponse createTodo(TodoRequest request) {

        LocalDateTime now = LocalDateTime.now();

        if (request.getDueDate() != null && request.getDueDate().isBefore(now)) {
            throw new IllegalArgumentException("dueDate must be in the future");
        }

        TodoItem todo = TodoItem.builder()
                .description(request.getDescription())
                .dueDate(request.getDueDate())
                .status(TodoStatus.NOT_DONE)
                .createdAt(now)
                .build();

        TodoItem saved = todoRepository.save(todo);

        log.info("Created todo {}", saved.getId());

        return mapToResponse(saved);
    }

    /**
     * Returns todos.
     */
    public List<TodoResponse> getTodos(boolean all) {

        List<TodoItem> todos = all
                ? todoRepository.findAll()
                : todoRepository.findByStatus(TodoStatus.NOT_DONE);

        todos.forEach(this::updatePastDueStatus);

        return todos.stream()
                .map(this::mapToResponse)
                .toList();
    }

    /**
     * Returns single todo.
     */
    public TodoResponse getTodoById(Long id) {

        TodoItem todo = getTodoOrThrow(id);

        updatePastDueStatus(todo);

        return mapToResponse(todo);
    }

    /**
     * Updates description.
     */
    @Transactional
    public TodoResponse updateDescription(Long id, String description) {

        TodoItem todo = getTodoOrThrow(id);

        updatePastDueStatus(todo);
        forbidPastDue(todo);

        todo.setDescription(description);

        log.info("Updated description for {}", id);

        return mapToResponse(todo);
    }

    /**
     * Marks todo DONE.
     */
    @Transactional
    public TodoResponse markDone(Long id) {

        TodoItem todo = getTodoOrThrow(id);

        updatePastDueStatus(todo);
        forbidPastDue(todo);

        todo.setStatus(TodoStatus.DONE);
        todo.setDoneAt(LocalDateTime.now());

        log.info("Marked {} DONE", id);

        return mapToResponse(todo);
    }

    /**
     * Marks todo NOT_DONE.
     */
    @Transactional
    public TodoResponse markNotDone(Long id) {

        TodoItem todo = getTodoOrThrow(id);

        updatePastDueStatus(todo);
        forbidPastDue(todo);

        todo.setStatus(TodoStatus.NOT_DONE);
        todo.setDoneAt(null);

        log.info("Marked {} NOT_DONE", id);

        return mapToResponse(todo);
    }

    /**
     * Retrieves todo or throws exception.
     */
    private TodoItem getTodoOrThrow(Long id) {

        return todoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Todo not found"));
    }

    /**
     * Updates status to PAST_DUE if needed.
     */
    private void updatePastDueStatus(TodoItem todo) {

        if (todo.getStatus() == TodoStatus.DONE) {
            return;
        }

        if (todo.getDueDate() != null &&
                todo.getDueDate().isBefore(LocalDateTime.now())) {

            todo.setStatus(TodoStatus.PAST_DUE);
            todoRepository.save(todo);

            log.info("Todo {} became PAST_DUE", todo.getId());
        }
    }

    /**
     * Prevent modification of past due todos.
     */
    private void forbidPastDue(TodoItem todo) {

        if (todo.getStatus() == TodoStatus.PAST_DUE) {

            log.warn("Attempt to modify PAST_DUE {}", todo.getId());

            throw new RuntimeException("Cannot modify PAST_DUE todo");
        }
    }

    /**
     * Maps entity to DTO.
     */
    private TodoResponse mapToResponse(TodoItem todo) {

        return TodoResponse.builder()
                .id(todo.getId())
                .description(todo.getDescription())
                .status(todo.getStatus().name())
                .createdAt(todo.getCreatedAt())
                .dueDate(todo.getDueDate())
                .doneAt(todo.getDoneAt())
                .build();
    }
}