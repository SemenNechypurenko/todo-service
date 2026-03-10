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
     * Create new todo
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
     * Get todos
     * default -> NOT_DONE only
     * all=true -> return all
     */
    public List<TodoResponse> getTodos(boolean all) {

        List<TodoItem> todos = all
                ? todoRepository.findAll()
                : todoRepository.findByStatus(TodoStatus.NOT_DONE);

        todos.forEach(this::updatePastDueStatus);

        return todos.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get todo by id
     */
    public TodoResponse getTodoById(Long id) {

        TodoItem todo = todoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Todo not found"));

        updatePastDueStatus(todo);

        return mapToResponse(todo);
    }

    /**
     * Update description
     */
    @Transactional
    public TodoResponse updateDescription(Long id, String description) {

        TodoItem todo = todoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Todo not found"));

        forbidPastDue(todo);

        todo.setDescription(description);

        log.info("Updated description for {}", id);

        return mapToResponse(todo);
    }

    /**
     * Mark as DONE
     */
    @Transactional
    public TodoResponse markDone(Long id) {

        TodoItem todo = todoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Todo not found"));

        forbidPastDue(todo);

        todo.setStatus(TodoStatus.DONE);
        todo.setDoneAt(LocalDateTime.now());

        log.info("Marked {} DONE", id);

        return mapToResponse(todo);
    }

    /**
     * Mark as NOT_DONE
     */
    @Transactional
    public TodoResponse markNotDone(Long id) {

        TodoItem todo = todoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Todo not found"));

        forbidPastDue(todo);

        todo.setStatus(TodoStatus.NOT_DONE);
        todo.setDoneAt(null);

        log.info("Marked {} NOT_DONE", id);

        return mapToResponse(todo);
    }

    /**
     * Update status to PAST_DUE if needed
     */
    private void updatePastDueStatus(TodoItem todo) {

        if (todo.getStatus() != TodoStatus.DONE
                && todo.getDueDate() != null
                && todo.getDueDate().isBefore(LocalDateTime.now())) {

            todo.setStatus(TodoStatus.PAST_DUE);
            todoRepository.save(todo);

            log.info("Todo {} became PAST_DUE", todo.getId());
        }
    }

    /**
     * Prevent modification of PAST_DUE
     */
    private void forbidPastDue(TodoItem todo) {

        if (todo.getStatus() == TodoStatus.PAST_DUE) {
            log.warn("Attempt to modify PAST_DUE {}", todo.getId());
            throw new RuntimeException("Cannot modify PAST_DUE todo");
        }
    }

    /**
     * Entity -> DTO
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