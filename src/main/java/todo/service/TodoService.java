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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TodoService {

    private final TodoRepository todoRepository;

    /**
     * Create a new todo item from DTO
     * @param request DTO containing description and dueDate
     * @return TodoResponse with created todo details
     */
    @Transactional
    public TodoResponse createTodo(TodoRequest request) {
        LocalDateTime now = LocalDateTime.now();

        if (request.getDueDate() != null && request.getDueDate().isBefore(now)) {
            throw new IllegalArgumentException("dueDate must be in the future");
        }

        // Build new todo entity
        TodoItem todo = TodoItem.builder()
                .description(request.getDescription())
                .dueDate(request.getDueDate())
                .status(TodoStatus.NOT_DONE)  // default status
                .createdAt(now)
                .build();

        TodoItem saved = todoRepository.save(todo);
        log.info("Created new todo with id {}", saved.getId());

        return mapToResponse(saved);
    }

    /**
     * Get all todos
     * @param all if true, include PAST_DUE items
     * @return list of TodoResponse DTOs
     */
    public List<TodoResponse> getTodos(boolean all) {
        List<TodoItem> todos = all ? todoRepository.findAll()
                : todoRepository.findByStatusNot(TodoStatus.PAST_DUE);

        // Automatically mark past due todos
        todos.forEach(this::updatePastDueStatus);

        return todos.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get todo by id
     * @param id todo ID
     * @return TodoResponse DTO
     */
    public TodoResponse getTodoById(Long id) {
        TodoItem todo = todoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Todo not found"));

        updatePastDueStatus(todo);
        return mapToResponse(todo);
    }

    /**
     * Update description of a todo
     * @param id todo ID
     * @param description new description
     * @return updated TodoResponse
     */
    @Transactional
    public TodoResponse updateDescription(Long id, String description) {
        TodoItem todo = todoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Todo not found"));

        forbidPastDue(todo);
        todo.setDescription(description);
        log.info("Updated description for todo {}", id);

        return mapToResponse(todo);
    }

    /**
     * Mark a todo as DONE
     * @param id todo ID
     * @return updated TodoResponse
     */
    @Transactional
    public TodoResponse markDone(Long id) {
        TodoItem todo = todoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Todo not found"));

        forbidPastDue(todo);
        todo.setStatus(TodoStatus.DONE);
        todo.setDoneAt(LocalDateTime.now());
        log.info("Marked todo {} as DONE", id);

        return mapToResponse(todo);
    }

    /**
     * Mark a todo as NOT_DONE
     * @param id todo ID
     * @return updated TodoResponse
     */
    @Transactional
    public TodoResponse markNotDone(Long id) {
        TodoItem todo = todoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Todo not found"));

        forbidPastDue(todo);
        todo.setStatus(TodoStatus.NOT_DONE);
        todo.setDoneAt(null);
        log.info("Marked todo {} as NOT_DONE", id);

        return mapToResponse(todo);
    }

    /**
     * Check and update PAST_DUE status if dueDate has passed
     */
    private void updatePastDueStatus(TodoItem todo) {
        if (todo.getStatus() != TodoStatus.DONE
                && todo.getDueDate() != null
                && todo.getDueDate().isBefore(LocalDateTime.now())) {
            todo.setStatus(TodoStatus.PAST_DUE);
            log.info("Todo {} is now PAST_DUE", todo.getId());
        }
    }

    /**
     * Prevent modification of PAST_DUE todos
     */
    private void forbidPastDue(TodoItem todo) {
        if (todo.getStatus() == TodoStatus.PAST_DUE) {
            log.warn("Attempt to modify PAST_DUE todo {}", todo.getId());
            throw new RuntimeException("Cannot modify PAST_DUE todo");
        }
    }

    /**
     * Map TodoItem entity to TodoResponse DTO
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
